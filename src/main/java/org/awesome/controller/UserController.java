package org.awesome.controller;

import org.awesome.constants.Constant;
import org.awesome.models.IdentifyCode;
import org.awesome.models.User;
import org.awesome.service.IIdentifyCodeService;
import org.awesome.service.IUserService;
import org.awesome.vo.LoginVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.SignupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IIdentifyCodeService identifyCodeService;

    @Autowired
    private HttpSession httpSession;

    @PostMapping("login")
    public RestResultVo login(@RequestBody LoginVo loginVo) {
        return userService.login(loginVo.getUsername(), loginVo.getPassword(), loginVo.getIdentifyCode());
    }

    @PostMapping("signUp")
    public RestResultVo signUp(@RequestBody SignupVo signupVo) {
        User user = new User();
        BeanUtils.copyProperties(signupVo, user);
        return userService.register(user);
    }

    @GetMapping("refreshToken")
    public RestResultVo refreshToken(@RequestHeader String authorization) {
        return userService.refreshToken(authorization);
    }

    @GetMapping("identifyCode")
    public String getIdentifyCode(HttpServletRequest request,HttpServletResponse response) {
        String username = request.getParameter("username");
        IdentifyCode identifyCode = identifyCodeService.generateIdentifyCode();
        httpSession.setAttribute(Constant.ATTRIBUTE_IDENTIFYCODE_KEY, identifyCode.getResult());
        return identifyCodeService.generateIdentifyCodeImage(identifyCode, response);
    }
}
