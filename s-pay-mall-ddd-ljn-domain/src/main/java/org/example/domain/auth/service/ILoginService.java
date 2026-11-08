package org.example.domain.auth.service;

import java.io.IOException;

/**
 * @author dabuw
 * @description 扫码登录领域服务
 * @date 11/10/26
 */
public interface ILoginService {

    /**
     * 生成扫码登录二维码 ticket
     */
    String createQrCodeTicket() throws Exception;

    /**
     * 前端轮询：根据 ticket 查询登录结果，返回 openid；未登录返回 null
     */
    String checkLogin(String ticket);

    /**
     * 扫码回调：记录登录态
     *
     * @param sceneId 二维码场景值（扫码事件里 EventKey 的值，去掉 qrscene_ 前缀）
     * @param openid  微信用户唯一标识
     */
    void saveLoginState(String sceneId, String openid) throws IOException;

}
