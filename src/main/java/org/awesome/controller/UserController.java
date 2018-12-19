package org.awesome.controller;

import com.alibaba.fastjson.JSON;
import org.awesome.Dao.RedisDao;
import org.awesome.models.User;
import org.awesome.service.IUserService;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.SignupVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("api/user/")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private RedisDao redisDao;

    /**
     * 用户退出登陆
     * @param username
     * @return
     */
    @GetMapping("logout")
    public RestResultVo logout(@RequestParam("username") String username) {
        if(StringUtils.isEmpty(username)){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户不能为空", null);
        }
        redisDao.del(username);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, null);
    }

    /**
     * 查询用户信息
     * @param username
     * @return
     */
    @GetMapping("getUserInfo")
    public User queryUserInfo(@RequestParam("username") String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        User user = null;
        if(redisDao.hasKey(username)){
            user = redisGetUser(username);
            System.out.println("redis取出："+JSON.toJSONString(user));
        }else{
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
            redisDao.set(username,JSON.toJSONString(user));
            System.out.println("数据库查出："+JSON.toJSONString(user));
        }
        return user;
    }

    /**
     * 保存
     * @param articleVo
     * @return
     */
    @PostMapping("saveNewArticle")
    public RestResultVo saveNewArticle(@RequestBody ArticleVo articleVo) {
        if(!"1".equals(articleVo.validateParams())){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, articleVo.validateParams(), null);
        }
        try{
            userService.saveNewArticle(articleVo);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
        }catch (Exception e){
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "保存信息异常", null);
        }
    }

    /*****************通用工具类**************************/

    private  User redisGetUser(String username){
        Object userObj = redisDao.get(username);
        if(userObj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(userObj),User.class);
    }


}
