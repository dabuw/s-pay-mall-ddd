package org.example.domain.order.service;

import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.order.adapter.port.IProductPort;
import org.example.domain.order.adapter.repository.IOrderRepository;
import org.example.domain.order.model.aggregate.CreateOrderAggregate;
import org.example.domain.order.model.entity.OrderEntity;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ProductEntity;
import org.example.domain.order.model.entity.ShopCarEntity;
import org.example.domain.order.model.valobj.OrderStatusVO;

import java.math.BigDecimal;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    protected final IOrderRepository repository;
    protected final IProductPort port;

    public AbstractOrderService(IOrderRepository repository, IProductPort port) {
        this.repository = repository;
        this.port = port;
    }

    @Override
    public PayOrderEntity createOrder(ShopCarEntity shopCarEntity) throws Exception {
        // 1. 查询当前用户是否存在掉单和未支付订单
        OrderEntity unpaidOrderEntity = repository.queryUnPayOrder(shopCarEntity);

        if (null != unpaidOrderEntity && OrderStatusVO.PAY_WAIT.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，已存在未支付订单。userId:{} productId:{} orderId:{}", shopCarEntity.getUserId(), shopCarEntity.getProductId(), unpaidOrderEntity.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrderEntity.getOrderId())
                    .payUrl(unpaidOrderEntity.getPayUrl())
                    .build();
        } else if (null != unpaidOrderEntity && OrderStatusVO.CREATE.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCarEntity.getUserId(), shopCarEntity.getProductId(), unpaidOrderEntity.getOrderId());
            PayOrderEntity payOrderEntity = doPrepayOrder(shopCarEntity.getUserId(), shopCarEntity.getProductId(), unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount());
            return PayOrderEntity.builder()
                    .orderId(payOrderEntity.getOrderId())
                    .payUrl(payOrderEntity.getPayUrl())
                    .build();
        }
        //2. 商品信息查询
        ProductEntity productEntity = port.queryProductByProductId(shopCarEntity.getProductId());

        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName());

        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCarEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        this.doSaveOrder(orderAggregate);

        PayOrderEntity payOrderEntity = doPrepayOrder(shopCarEntity.getUserId(), productEntity.getProductId(), productEntity.getProductName(),orderEntity.getOrderId(),productEntity.getPrice());
        log.info("创建订单-完成，生成支付单。userId: {} orderId: {} payUrl: {}", shopCarEntity.getUserId(), orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        //3.
        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .build();
    }


    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;


}