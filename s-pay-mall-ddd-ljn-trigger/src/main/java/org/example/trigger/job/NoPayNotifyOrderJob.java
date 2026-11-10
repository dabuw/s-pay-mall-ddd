package org.example.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.order.service.IOrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description 检测未接收到或未正确处理的支付回调通知
 * <p>
 * 说明：
 * 1. 默认每 30 秒执行一次（原来 3 秒一次，支付宝沙箱故障时会疯狂调用接口并刷日志）；
 * 2. 内置熔断：连续失败 {@link #MAX_CONSECUTIVE_FAILURES} 次后暂停 {@link #PAUSE_MILLIS}
 * 毫秒，避免平台故障期间持续无效调用；
 * 3. 单个订单失败不影响同批次其它订单。
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    /** 连续失败多少次后熔断 */
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    /** 熔断时长：5 分钟 */
    private static final long PAUSE_MILLIS = 5 * 60 * 1000L;

    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;

    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile long pausedUntil = 0L;

    @Scheduled(cron = "${job.no-pay-notify.cron:0/30 * * * * ?}")
    public void exec() {
        // 熔断期内直接跳过，不再打日志也不调接口
        if (System.currentTimeMillis() < pausedUntil) {
            return;
        }

        List<String> orderIds;
        try {
            orderIds = orderService.queryNoPayNotifyOrder();
        } catch (Exception e) {
            log.warn("补偿任务：查询待补偿订单失败", e);
            return;
        }
        if (null == orderIds || orderIds.isEmpty()) {
            return;
        }

        log.info("任务；检测未接收到或未正确处理的支付回调通知，待补偿订单数：{}", orderIds.size());

        for (String orderId : orderIds) {
            if (System.currentTimeMillis() < pausedUntil) {
                log.warn("补偿任务已熔断，本轮剩余 {} 个订单跳过", orderIds.size());
                return;
            }
            try {
                AlipayTradeQueryResponse response = query(orderId);
                String code = response.getCode();
                if ("10000".equals(code)) {
                    // 注意：code=10000 只代表查询成功，是否已支付要看 trade_status。
                    // 之前直接按 code 判断，会把 WAIT_BUYER_PAY 的订单误标成已支付。
                    String tradeStatus = response.getTradeStatus();
                    if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                        orderService.changeOrderPaySuccess(orderId);
                    }
                    consecutiveFailures.set(0);
                } else {
                    // 20000 一般是平台异常（如 ACQ.SYSTEM_ERROR / aop.unknow-error）
                    recordFailure(response.getSubCode(), response.getSubMsg());
                }
            } catch (Exception e) {
                recordFailure(e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    private AlipayTradeQueryResponse query(String orderId) throws Exception {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
        bizModel.setOutTradeNo(orderId);
        request.setBizModel(bizModel);
        return alipayClient.execute(request);
    }

    /**
     * 记录一次失败；达到阈值就熔断一段时间，避免平台故障时持续无效调用。
     */
    private void recordFailure(String code, String message) {
        int failures = consecutiveFailures.incrementAndGet();
        if (failures >= MAX_CONSECUTIVE_FAILURES) {
            pausedUntil = System.currentTimeMillis() + PAUSE_MILLIS;
            consecutiveFailures.set(0);
            log.warn("补偿查单连续失败 {} 次，暂停补偿任务 {} 分钟。最近错误：{} - {}",
                    MAX_CONSECUTIVE_FAILURES, PAUSE_MILLIS / 60000, code, message);
        } else {
            log.warn("补偿查单失败（{}）code：{} message：{}", failures, code, message);
        }
    }

}
