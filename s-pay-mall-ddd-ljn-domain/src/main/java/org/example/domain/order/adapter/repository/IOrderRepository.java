package org.example.domain.order.adapter.repository;

import org.example.domain.order.model.aggregate.CreateOrderAggregate;
import org.example.domain.order.model.entity.OrderEntity;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ShopCarEntity;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
public interface IOrderRepository {
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    OrderEntity queryUnPayOrder(ShopCarEntity shopCartEntity);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);


}

