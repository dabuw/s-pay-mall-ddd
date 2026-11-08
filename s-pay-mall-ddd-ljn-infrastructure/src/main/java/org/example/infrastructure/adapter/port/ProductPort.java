package org.example.infrastructure.adapter.port;

import org.example.domain.order.adapter.port.IProductPort;
import org.example.domain.order.model.entity.ProductEntity;
import org.example.infrastructure.gateway.ProductRPC;
import org.example.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Component
public class ProductPort implements IProductPort {

    private final ProductRPC productRPC;

    public ProductPort(ProductRPC productRPC) {
        this.productRPC = productRPC;
    }

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        return toEntity(productRPC.queryProductByProductId(productId));
    }

    @Override
    public List<ProductEntity> queryProductList() {
        List<ProductEntity> list = new ArrayList<>();
        for (ProductDTO productDTO : productRPC.queryProductList()) {
            list.add(toEntity(productDTO));
        }
        return list;
    }

    private ProductEntity toEntity(ProductDTO productDTO) {
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }

}