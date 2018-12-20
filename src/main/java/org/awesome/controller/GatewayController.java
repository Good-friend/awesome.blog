package org.awesome.controller;

import org.awesome.constants.Constant;
import org.awesome.models.UpdateBlog;
import org.awesome.models.User;
import org.awesome.service.IGatewayService;
import org.awesome.service.IIdentifyCodeService;
import org.awesome.service.impl.MongoService;
import org.awesome.vo.LoginVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.SignupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/gateway/")
public class GatewayController {

    @Autowired
    private IGatewayService gatewayService;

    @Autowired
    private IIdentifyCodeService identifyCodeService;
    @Resource
    private MongoService mongoService;

    @PostMapping("login")
    public RestResultVo login(@RequestBody LoginVo loginVo) {
        return gatewayService.login(loginVo.getUsername(), loginVo.getPassword(), loginVo.getIdentifyCode());
    }

    @PostMapping("signUp")
    public RestResultVo signUp(@RequestBody SignupVo signupVo) {
        User user = new User();
        BeanUtils.copyProperties(signupVo, user);
        return gatewayService.register(user);
    }

    @GetMapping("refreshToken")
    public RestResultVo refreshToken(@RequestHeader(Constant.JWT_HEADER) String authorization) {
        return gatewayService.refreshToken(authorization);
    }

    @GetMapping("identifyCode")
    public RestResultVo getIdentifyCode(@RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户不能为空", null);
        }

        return identifyCodeService.generateIdentifyCodeAndImage(username);
    }

    @GetMapping("queryUpdateBlogs")
    public RestResultVo queryUpdateBlogs(HttpServletRequest request){
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.queryUpdateBlogList());
    }
    @PostMapping("saveUpdateBlogs")
    public RestResultVo saveUpdateBlogs(@RequestBody UpdateBlog UpdateBlog){
        mongoService.saveUpdateBlog(UpdateBlog);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
    }

}
