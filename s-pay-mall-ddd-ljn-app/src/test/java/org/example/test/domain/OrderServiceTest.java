package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.order.model.entity.PayOrderEntity;
import org.example.domain.order.model.entity.ShopCarEntity;
import org.example.domain.order.service.IOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author dabuw
 * @description
 * @date 20/10/26
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Resource
    private IOrderService orderService;

    @Test
    public void test_createOrder() throws Exception {
        ShopCarEntity shopCarEntity = new ShopCarEntity();
        shopCarEntity.setUserId("dabuw");
        shopCarEntity.setProductId("10001");
        PayOrderEntity payOrderEntity = orderService.createOrder(shopCarEntity);
        log.info("请求参数:{}", JSON.toJSONString(shopCarEntity));
        log.info("测试结果:{}", JSON.toJSONString(payOrderEntity));
    }

}