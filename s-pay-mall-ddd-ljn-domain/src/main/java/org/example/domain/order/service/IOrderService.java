package org.example.domain.order.service;

import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ShopCarEntity;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
public interface IOrderService {

    PayOrderEntity createOrder(ShopCarEntity shopCartEntity) throws Exception;

}
