package org.example.domain.order.service;

import org.example.domain.order.model.entity.OrderEntity;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ShopCarEntity;

import java.util.List;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
public interface IOrderService {

    PayOrderEntity createOrder(ShopCarEntity shopCartEntity) throws Exception;

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    /** 查询单个订单 */
    OrderEntity queryOrder(String orderId);

    /** 查询订单列表 */
    List<OrderEntity> queryOrderList(String userId);

}
