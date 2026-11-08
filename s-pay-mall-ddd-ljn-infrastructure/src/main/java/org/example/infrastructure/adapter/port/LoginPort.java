package org.example.infrastructure.adapter.port;

import com.google.common.cache.Cache;
import org.example.domain.auth.adapter.port.ILoginPort;
import org.example.infrastructure.gateway.IWeixinApiService;
import org.example.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import org.example.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import org.example.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import org.example.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dabuw
 * @description 微信公众号登录能力实现
 * @date 11/10/26
 */
@Service
public class LoginPort implements ILoginPort {

    @Value("${weixin.config.app-id}")
    private String appid;
    @Value("${weixin.config.app-secret}")
    private String appSecret;
    @Value("${weixin.config.template_id}")
    private String template_id;

    @Resource
    private Cache<String, String> weixinAccessToken;

    @Resource
    private IWeixinApiService weixinApiService;

    @Override
    public String createQrCodeTicket(int sceneId) throws IOException {
        // 1. 获取 accessToken
        String accessToken = getAccessToken();

        // 2. 生成带参临时二维码，sceneId 用于关联扫码事件
        WeixinQrCodeRequestDTO weixinQrCodeReq = WeixinQrCodeRequestDTO.builder()
                .expire_seconds(300)
                .action_name(WeixinQrCodeRequestDTO.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeRequestDTO.ActionInfo.builder()
                        .scene(WeixinQrCodeRequestDTO.ActionInfo.Scene.builder()
                                .scene_id(sceneId)
                                .build())
                        .build())
                .build();

        Call<WeixinQrCodeResponseDTO> call = weixinApiService.createQrCode(accessToken, weixinQrCodeReq);
        WeixinQrCodeResponseDTO weixinQrCodeRes = call.execute().body();
        if (null == weixinQrCodeRes || null == weixinQrCodeRes.getTicket()) {
            throw new IOException("生成微信二维码失败，请检查公众号配置");
        }
        return weixinQrCodeRes.getTicket();
    }

    @Override
    public void sendLoginTemplate(String openid) throws IOException {
        String accessToken = getAccessToken();

        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, openid);

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO(openid, template_id);
        templateMessageDTO.setUrl("https://dabuw.online/");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();
    }

    /**
     * 获取并缓存 accessToken
     */
    private String getAccessToken() throws IOException {
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null != accessToken) {
            return accessToken;
        }
        Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
        WeixinTokenResponseDTO weixinTokenRes = call.execute().body();
        if (null == weixinTokenRes || null == weixinTokenRes.getAccess_token()) {
            throw new IOException("获取微信 access_token 失败，请检查 appId / appSecret");
        }
        accessToken = weixinTokenRes.getAccess_token();
        weixinAccessToken.put(appid, accessToken);
        return accessToken;
    }

}
