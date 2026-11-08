package org.example.domain.order.adapter.port;

import org.example.domain.order.model.entity.ProductEntity;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);

}