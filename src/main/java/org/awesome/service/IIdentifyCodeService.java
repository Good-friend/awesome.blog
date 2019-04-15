package org.awesome.service;

import org.awesome.vo.RestResultVo;

import javax.servlet.http.HttpServletRequest;

public interface IIdentifyCodeService {

    RestResultVo generateIdentifyCodeAndImage(String username);

    RestResultVo checkIdentifyCode(HttpServletRequest request);
}
