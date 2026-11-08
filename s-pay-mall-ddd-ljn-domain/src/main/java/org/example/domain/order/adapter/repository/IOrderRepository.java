package org.example.domain.order.adapter.repository;

import org.example.domain.order.model.aggregate.CreateOrderAggregate;
import org.example.domain.order.model.entity.OrderEntity;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ShopCarEntity;

import java.util.List;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
public interface IOrderRepository {
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    OrderEntity queryUnPayOrder(ShopCarEntity shopCartEntity);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);


    void changeOrderPaySuccess(String orderId);

    /** 按订单号查询订单 */
    OrderEntity queryOrderByOrderId(String orderId);

    /** 查询订单列表，userId 为空时返回全部 */
    List<OrderEntity> queryOrderList(String userId, int limit);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);
}

