package org.awesome.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.awesome.Dao.RedisDao;
import org.awesome.constants.Constant;
import org.awesome.models.Comment;
import org.awesome.models.UpdateBlog;
import org.awesome.models.User;
import org.awesome.service.ICatalogueService;
import org.awesome.service.IUserService;
import org.awesome.service.impl.MongoService;
import org.awesome.utils.CommonUtils;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.SignupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/user/")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private RedisDao redisDao;
    @Resource
    private MongoService mongoService;
    @Autowired
    private ICatalogueService catalogueService;

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
    public RestResultVo queryUserInfo(@RequestParam("username") String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        User user = userService.redisGetUser(username);
        if(user == null){
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
            redisDao.set(username,JSON.toJSONString(user));
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, user);
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
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, articleVo.getUsername());
        Object code = redisDao.get(key);
        redisDao.del(key);
        if (code == null || ((int) code) != Integer.parseInt(articleVo.getVercode())) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }
        try{
           String serialNumber = userService.saveNewArticle(articleVo);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", serialNumber);
        }catch (Exception e){
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "保存信息异常", null);
        }
    }

    @GetMapping("queryEditArticle")
    public RestResultVo saveNewArticle(@RequestParam("id")String id) {
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", catalogueService.queryConnotationDetail(id));
    }

    @PostMapping("updateArticle")
    public RestResultVo updateArticle(@RequestBody JSONObject obj) {
        System.out.println(JSON.toJSONString(obj));
        String serialNumber = obj.getString("serialNumber");
        String type = obj.getString("type");
        String content = obj.getString("content");
        String title = obj.getString("title");
        String vercode = obj.getString("vercode");
        String username = obj.getString("author");
        if(StringUtils.isEmpty(serialNumber)
                ||StringUtils.isEmpty(type)
                ||StringUtils.isEmpty(content)
                ||StringUtils.isEmpty(title)
                ||StringUtils.isEmpty(vercode)
                ||StringUtils.isEmpty(username)){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改失败：参数为空",null);
        }
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, username);
        Object code = redisDao.get(key);
        redisDao.del(key);
        if (code == null || ((int) code) != Integer.parseInt(vercode)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }
        try {
            userService.updateArticle(serialNumber,type,content,title);
        }catch (Exception e){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改异常："+e.getMessage(),null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",null);

    }

    /**
     * 更新网站日志
     * @param UpdateBlog
     * @return
     */
    @PostMapping("saveUpdateBlogs")
    public RestResultVo saveUpdateBlogs(@RequestBody UpdateBlog UpdateBlog){
        mongoService.saveUpdateBlog(UpdateBlog);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
    }

    @PostMapping("/saveComment")
    public RestResultVo saveComment(@RequestBody Comment comment){
        mongoService.saveComment(comment);
        catalogueService.updateCommentTimes(comment.getSerialNumber());
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.queryCommentBySerialNumber(comment.getSerialNumber()));
    }

    @GetMapping("/delComment")
    public RestResultVo delComment(@RequestParam("id")String id,@RequestParam("serialNumber")String serialNumber){
        List<Comment> list = null;
        if(mongoService.delComment(id).getDeletedCount() == 1){
            list = mongoService.queryCommentBySerialNumber(serialNumber);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",list);
    }

    @GetMapping("/dealArticle")
    public RestResultVo dealArticle(@RequestParam("id")String id,@RequestParam("type")String type,@RequestParam("val")boolean val){
        if("del".equals(type)){
            catalogueService.deleteCatalogueDetail(id);
            catalogueService.deleteConnotation(id);
        }
        if("best".equals(type)){
            catalogueService.updateCatalogueBest(id,val);
        }
        if("stick".equals(type)){
            catalogueService.updateCatalogueStick(id,val);
        }
        if("publicity".equals(type)){
            catalogueService.updateCataloguePublicity(id,val);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",null);
    }
    /*****************通用工具类**************************/



}
