package org.example.domain.auth.adapter.port;

import java.io.IOException;

/**
 * @author dabuw
 * @description 登录能力端口：生成扫码二维码、发送登录模板消息
 * @date 11/10/26
 */
public interface ILoginPort {

    /**
     * 生成带参二维码 ticket
     *
     * @param sceneId 场景值，用于和扫码事件做关联
     * @return 二维码 ticket（前端用于展示图片）
     */
    String createQrCodeTicket(int sceneId) throws IOException;

    /**
     * 发送登录成功模板消息
     *
     * @param openid 微信用户唯一标识
     */
    void sendLoginTemplate(String openid) throws IOException;

}
