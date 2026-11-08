package org.example.domain.auth.service;

import java.io.IOException;

/**
 * @author dabuw
 * @description
 * @date 11/10/26
 */
public interface ILoginService {

    String createQrCodeTicket() throws Exception;

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openid) throws IOException;

}
