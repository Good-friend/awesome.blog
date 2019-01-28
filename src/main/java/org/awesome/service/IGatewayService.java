package org.awesome.service;

import org.awesome.models.User;
import org.awesome.vo.RestResultVo;

import javax.servlet.http.HttpServletRequest;

public interface IGatewayService {
    RestResultVo login(String username, String password, int identifyCode,HttpServletRequest request);

    RestResultVo register(User user);

    RestResultVo refreshToken(String oldToken);

    void registerSendEmail(String nickname,String username,String password,String email);
}
