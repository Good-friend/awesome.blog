package org.awesome.controller;

import com.alibaba.fastjson.JSONObject;
import org.awesome.Dao.RedisDao;
import org.awesome.constants.Constant;
import org.awesome.models.OperationFlow;
import org.awesome.models.UpdateBlog;
import org.awesome.models.User;
import org.awesome.service.IGatewayService;
import org.awesome.service.IIdentifyCodeService;
import org.awesome.service.impl.MongoService;
import org.awesome.utils.CommonUtils;
import org.awesome.utils.EmailUtils;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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
    public RestResultVo login(@RequestBody LoginVo loginVo,HttpServletRequest request) {
        return gatewayService.login(loginVo.getUsername(), loginVo.getPassword()/*, loginVo.getIdentifyCode()*/,request);
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
    public RestResultVo getIdentifyCode(HttpServletRequest request) {
        return identifyCodeService.generateIdentifyCodeAndImage(CommonUtils.getIpAddress(request));
    }

    @GetMapping("checkIdentifyCode")
    public RestResultVo checkIdentifyCode(HttpServletRequest request) {
        return identifyCodeService.checkIdentifyCode(request);
    }

    @GetMapping("queryUpdateBlogs")
    public RestResultVo queryUpdateBlogs(HttpServletRequest request){
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.queryUpdateBlogList());
    }

    @GetMapping("registerValidate")
    public RestResultVo registerValidate(HttpServletRequest request) {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        return gatewayService.registerValidate(username,email);
    }
    /**
     * @param vo
     * @return
     */
    @PostMapping("register")
    public RestResultVo register(@RequestBody SignupVo vo) {
        String valiStr = vo.validate();
        if (!"1".equals(valiStr)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "注册失败：" + valiStr, null);
        }

        String password = CommonUtils.getStringRandom(8);
        User user = new User(vo.getUsername(), password, "1", 0, vo.getNickname(), "1".equals(vo.getSex()) ? "http://120.79.240.9:8080/headImg/6.jpg" : "http://120.79.240.9:8080/headImg/8.jpg", vo.getSex(), "地球", CommonUtils.getNowDate(), "大家在这相聚是缘分", vo.getEmail(), null);
        RestResultVo restResultVo = gatewayService.register(user);
        if (restResultVo.getCode() == RestResultVo.RestResultCode.SUCCESS) {
            gatewayService.registerSendEmail(vo.getNickname(),vo.getUsername(),password,vo.getEmail());
        }
        List<String> list = new ArrayList<>();
        list.add("USER");
        user.setAuthorities(list);
        restResultVo.setData(user);
        return restResultVo;
    }

}
