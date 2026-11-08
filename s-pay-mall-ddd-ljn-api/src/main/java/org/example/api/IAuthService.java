package org.example.api;

import org.example.api.response.Response;

/**
 * @author dabuw
 * @description
 * @date 11/10/26
 */
public interface IAuthService {

    Response<String> weixinQrCodeTicket();

    Response<String> checkLogin(String ticket);

}

