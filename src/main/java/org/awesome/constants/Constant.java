package org.awesome.constants;

/**
 * 常量接口
 */
public interface Constant {
    /**
     * jwt token前缀
     */
    String JWT_PREFIX = "Bearer ";

    /**
     * jwt请求头
     */
    String JWT_HEADER = "Authorization";

    /**
     * jwt失效时间
     */
    int JWT_TIMEOUT = 60 * 60 * 24 * 1000 * 3;

    /**
     * redis验证码key
     */
    String REDIS_IDENTIFYCODE_KEY_WRAPPER = "identifyCodeValidate[{0}]";

    /**
     * redis验证码失效时间
     */
    int REDIS_IDENTIFYCODE_TIMEOUT = 60;

    /**
     * redis token黑名单失效时间
     */
    int REDIS_TOKEN_TIMEOUT = 30;
}
