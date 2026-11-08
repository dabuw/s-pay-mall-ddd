package org.example.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.example.api.response.Response;
import org.example.domain.order.model.entity.OrderEntity;
import org.example.domain.order.service.IOrderService;
import org.example.types.common.Constants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dabuw
 * @description 订单查询接口：支付结果轮询、我的订单列表
 * @date 8/11/26
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/order/")
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * http://localhost:8080/api/v1/order/status?orderId=xxx
     */
    @GetMapping("status")
    public Response<OrderEntity> queryOrderStatus(@RequestParam String orderId) {
        try {
            OrderEntity orderEntity = orderService.queryOrder(orderId);
            if (null == orderEntity) {
                return Response.<OrderEntity>builder()
                        .code(Constants.ResponseCode.UN_ERROR.getCode())
                        .info("订单不存在 orderId:" + orderId)
                        .build();
            }
            return Response.<OrderEntity>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(orderEntity)
                    .build();
        } catch (Exception e) {
            log.error("查询订单状态失败 orderId:{}", orderId, e);
            return Response.<OrderEntity>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * http://localhost:8080/api/v1/order/list?userId=liujiani
     */
    @GetMapping("list")
    public Response<List<OrderEntity>> queryOrderList(@RequestParam(required = false) String userId) {
        try {
            List<OrderEntity> orderList = orderService.queryOrderList(userId);
            return Response.<List<OrderEntity>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(orderList)
                    .build();
        } catch (Exception e) {
            log.error("查询订单列表失败 userId:{}", userId, e);
            return Response.<List<OrderEntity>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
