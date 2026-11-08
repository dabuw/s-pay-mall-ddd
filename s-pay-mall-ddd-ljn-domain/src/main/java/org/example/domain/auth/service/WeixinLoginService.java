package org.example.domain.auth.service;

import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.auth.adapter.port.ILoginPort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dabuw
 * @description 微信扫码登录领域服务
 * <p>
 * 二维码 ticket 与扫码事件之间的关联：
 * 1. 生成二维码时随机分配 sceneId，并记录 sceneId -> ticket；
 * 2. 用户扫码后，微信推送的事件里 EventKey 是 sceneId（未关注用户带 qrscene_ 前缀）；
 * 3. 用 sceneId 找回 ticket，把 openid 写入 openidToken，前端轮询 ticket 即可拿到登录结果。
 */
@Slf4j
@Service
class WeixinLoginService implements ILoginService {

    @Resource
    private ILoginPort loginPort;

    @Resource
    private Cache<String, String> openidToken;

    @Resource(name = "loginSceneTicket")
    private Cache<String, String> loginSceneTicket;

    @Override
    public String createQrCodeTicket() throws Exception {
        // sceneId 需要是 32 位整型，随机生成保证每次登录互不干扰
        int sceneId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        String ticket = loginPort.createQrCodeTicket(sceneId);
        loginSceneTicket.put(String.valueOf(sceneId), ticket);
        log.info("生成扫码登录二维码 sceneId:{} ticket:{}", sceneId, ticket);
        return ticket;
    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }

    @Override
    public void saveLoginState(String sceneId, String openid) throws IOException {
        String ticket = loginSceneTicket.getIfPresent(sceneId);
        if (null == ticket) {
            log.warn("扫码场景已失效，忽略本次登录 sceneId:{} openid:{}", sceneId, openid);
            return;
        }
        // 保存登录信息
        openidToken.put(ticket, openid);
        // 发送模板消息（测试号、模板未配置等情况不应影响登录）
        try {
            loginPort.sendLoginTemplate(openid);
        } catch (Exception e) {
            log.warn("发送登录成功模板消息失败 openid:{}", openid, e);
        }
    }

}
