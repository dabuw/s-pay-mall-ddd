package org.example.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCarEntity {

    private String userId;

    private String productId;

}
