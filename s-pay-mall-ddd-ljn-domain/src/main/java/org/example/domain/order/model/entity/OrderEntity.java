package org.example.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.order.model.valobj.OrderStatusVO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    private String productId;
    private String productName;
    private String orderId;
    private Date orderTime;
    private BigDecimal totalAmount;
    private OrderStatusVO orderStatusVO;
    private String payUrl;

}