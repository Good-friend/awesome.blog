package org.awesome.service;

import org.awesome.models.User;
import org.awesome.vo.RestResultVo;

import java.util.List;

public interface IUserService {

    User findUserByName(String username);

    List<String> findUserAuthoritiesByName(String username);

    RestResultVo login(String username, String password, int identifyCode);

    RestResultVo register(User user);

    RestResultVo refreshToken(String oldToken);
}
