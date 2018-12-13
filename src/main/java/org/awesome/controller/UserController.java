package org.awesome.controller;

import com.alibaba.fastjson.JSONObject;
import org.awesome.Dao.RedisDao;
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

import javax.annotation.Resource;
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

    @Resource
    private RedisDao redisDao;

    @PostMapping("login")
    public RestResultVo login(@RequestBody LoginVo loginVo) {
        return userService.login(loginVo.getUsername(), loginVo.getPassword(), loginVo.getIdentifyCode());
    }
    @GetMapping("logout")
    public String logout(@RequestParam("username") String username) {
        if(username == null || username ==""){
            return "0";
        }
        redisDao.del("["+username+"]info");
        return "1";
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

    /**
     * 验证码获取
     * @param request
     * @param response
     * @return
     */
    @GetMapping("identifyCode")
    public JSONObject getIdentifyCode(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resObj = new JSONObject();
        resObj.put("result","0");
        String username = request.getParameter("username");
        if(username == null || username ==""){
            resObj.put("failReason","用户名为空");
            return resObj;
        }
        IdentifyCode identifyCode = identifyCodeService.generateIdentifyCode();
        redisDao.set("validate["+username+"]",identifyCode.getResult());
        resObj.put("result","1");
        resObj.put("img",identifyCodeService.generateIdentifyCodeImage(identifyCode, response));
        //httpSession.setAttribute(Constant.ATTRIBUTE_IDENTIFYCODE_KEY, identifyCode.getResult());
        return resObj;
    }
}
