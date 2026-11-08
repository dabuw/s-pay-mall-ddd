package org.example.domain.order.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.order.adapter.port.IProductPort;
import org.example.domain.order.adapter.repository.IOrderRepository;
import org.example.domain.order.model.aggregate.CreateOrderAggregate;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.valobj.OrderStatusVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Slf4j
@Service
public class OrderService extends AbstractOrderService{

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;
    // page=电脑网站支付（默认），wap=手机网站支付
    @Value("${alipay.pay_mode:page}")
    private String payMode;

    @Resource
    private AlipayClient alipayClient;

    public OrderService(IOrderRepository repository, IProductPort port) {
        super(repository, port);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        repository.doSaveOrder(orderAggregate);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", productName);

        String form;
        if ("wap".equalsIgnoreCase(payMode)) {
            // 手机网站支付：沙箱电脑网站支付当前故障（SYSTEM_ERROR），联调期用 wap 跑通链路
            bizContent.put("product_code", "QUICK_WAP_WAY");
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(returnUrl);
            request.setBizContent(bizContent.toString());
            form = alipayClient.pageExecute(request).getBody();
        } else {
            // 电脑网站支付
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(returnUrl);
            request.setBizContent(bizContent.toString());
            form = alipayClient.pageExecute(request).getBody();
        }

        log.info("生成支付表单完成 userId:{} orderId:{} payMode:{}", userId, orderId, payMode);

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setOrderStatus(OrderStatusVO.PAY_WAIT);

        repository.updateOrderPayInfo(payOrderEntity);

        return payOrderEntity;
    }


}
