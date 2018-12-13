package org.awesome.service;

import org.awesome.models.IdentifyCode;

import javax.servlet.http.HttpServletResponse;

public interface IIdentifyCodeService {

    IdentifyCode generateIdentifyCode();

    String generateIdentifyCodeImage(IdentifyCode identifyCode, HttpServletResponse response);
}
