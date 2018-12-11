package org.awesome.aspect;

import org.awesome.vo.RestResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "org.awesome.controller")
public class ControllerAdvice {

    @ExceptionHandler({RuntimeException.class})
    RestResultVo handleException(HttpServletRequest request, Throwable ex) {
        return new RestResultVo(RestResultVo.RestResultCode.FAILED, "Ah, server error.", null);
    }
}
