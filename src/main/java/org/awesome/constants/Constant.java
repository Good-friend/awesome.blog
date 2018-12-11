package org.awesome.constants;

/**
 * 常量接口
 */
public interface Constant {
    /**
     * jwt token前缀
     */
    public static final String JWT_PREFIX = "Bearer ";

    /**
     * jwt请求头
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * session验证码key
     */
    public static final String ATTRIBUTE_IDENTIFYCODE_KEY = "code";
}
