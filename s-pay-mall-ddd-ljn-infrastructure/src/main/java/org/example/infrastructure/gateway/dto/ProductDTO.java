package org.example.infrastructure.gateway.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Data
public class ProductDTO {

    /** 商品ID */
    private String productId;
    /** 商品名称 */
    private String productName;
    /** 商品描述 */
    private String productDesc;
    /** 商品价格 */
    private BigDecimal price;

}
