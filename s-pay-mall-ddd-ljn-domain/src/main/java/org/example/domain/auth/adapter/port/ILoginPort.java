package org.example.domain.auth.adapter.port;

import java.io.IOException;

/**
 * @author dabuw
 * @description
 * @date 11/10/26
 */
public interface ILoginPort {
    String createQrCodeTicket() throws IOException;

    void sendLoginTemplate(String openid) throws IOException;
}
