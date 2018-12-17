package org.awesome.service;

import org.awesome.models.User;
import org.awesome.vo.RestResultVo;

public interface IGatewayService {
    RestResultVo login(String username, String password, int identifyCode);

    RestResultVo register(User user);

    RestResultVo refreshToken(String oldToken);
}
