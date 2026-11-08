package org.example.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.infrastructure.dao.po.PayOrder;

import java.util.List;

@Mapper
public interface IOrderDao {

    void insert(PayOrder payOrder);

    PayOrder queryUnPayOrder(PayOrder payOrder);

    PayOrder queryOrderByOrderId(@Param("orderId") String orderId);

    List<PayOrder> queryOrderList(@Param("userId") String userId, @Param("limit") int limit);

    void updateOrderPayInfo(PayOrder payOrder);

    void changeOrderPaySuccess(PayOrder payOrderReq);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(@Param("orderId") String orderId);
}
