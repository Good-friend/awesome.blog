package org.awesome.service;

import org.awesome.vo.RestResultVo;

public interface IIdentifyCodeService {

    RestResultVo generateIdentifyCodeAndImage(String username);
}
