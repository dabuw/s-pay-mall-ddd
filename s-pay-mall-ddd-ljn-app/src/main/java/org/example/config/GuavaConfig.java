package org.example.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;
import org.example.trigger.listener.OrderPaySuccessListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaConfig {

    @Bean(name = "weixinAccessToken")
    public Cache<String, String> weixinAccessToken() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.HOURS)
                .build();
    }

    @Bean(name = "openidToken")
    public Cache<String, String> openidToken() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }
    /**
     * 扫码登录：sceneId -> ticket，二维码 5 分钟有效
     */
    @Bean(name = "loginSceneTicket")
    public Cache<String, String> loginSceneTicket() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public EventBus eventBusListener(OrderPaySuccessListener listener){
        EventBus eventBus = new EventBus();
        eventBus.register(listener);
        return eventBus;
    }


}
