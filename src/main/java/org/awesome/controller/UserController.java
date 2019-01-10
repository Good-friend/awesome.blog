package org.awesome.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.awesome.Dao.RedisDao;
import org.awesome.constants.Constant;
import org.awesome.enums.Role;
import org.awesome.models.*;
import org.awesome.service.ICatalogueService;
import org.awesome.service.IFavoriteService;
import org.awesome.service.IGatewayService;
import org.awesome.service.IUserService;
import org.awesome.service.impl.MongoService;
import org.awesome.utils.CommonUtils;
import org.awesome.utils.EmailUtils;
import org.awesome.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.*;

@RestController
@RequestMapping("api/user/")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private MongoService mongoService;
    @Autowired
    private ICatalogueService catalogueService;
    @Autowired
    private IGatewayService gatewayService;
    @Autowired
    private IFavoriteService favoriteService;

    /**
     * 用户退出登陆
     *
     * @param username
     * @return
     */
    @GetMapping("logout")
    public RestResultVo logout(@RequestParam("username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户不能为空", null);
        }
        redisDao.del(username);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, null);
    }

    /**
     * 查询用户信息
     *
     * @param username
     * @return
     */
    @GetMapping("getUserInfo")
    public RestResultVo queryUserInfo(@RequestParam("username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "参数有误，无权操作", null);
        }
        User user = userService.redisGetUser(username);
        if (user == null) {
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
            redisDao.set(username, JSON.toJSONString(user));
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, user);
    }

    @GetMapping("getAllUserInfo")
    public RestResultVo getAllUserInfo(@RequestParam("username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "参数有误，无权操作", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user) || !Role.ADMIN.getValue().equals(user.getAuthorities().get(0))) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权操作", null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null,userService.findAllUser());
    }


    //生成随机数字和字母,
    private static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();
        //length为几位密码
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * @param vo
     * @return
     */
    @PostMapping("adminSignUp")
    public RestResultVo adminSignUp(@RequestBody SignupVo vo) {
        String valiStr = vo.validate();
        if (!"1".equals(valiStr)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "注册失败：" + valiStr, null);
        }
        String vercode = vo.getVercode();
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, vo.getUsername());
        Object code = redisDao.get(key);
        redisDao.del(key);
        if (code == null || ((int) code) != Integer.parseInt(vercode)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }
        String password = getStringRandom(8);
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

    @PostMapping("updateUserBasicInfo")
    public RestResultVo updateUserBasicInfo(@RequestBody UserBasicInfoVo userBasicInfoVo) {
        String username = userBasicInfoVo.getUsername();
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "保存信息出错：用户名空啦", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "保存信息出错：用户名没查着", null);
        }
        try {
            userService.updateUserBasicInfo(userBasicInfoVo);
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
            redisDao.set(username, JSON.toJSONString(user));
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "保存信息异常", null);
        }
    }

    @PostMapping("updateUserPassword")
    public RestResultVo updateUserBasicInfo(@RequestBody JSONObject object) {
        String username = object.getString("username");
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改密码出错：用户名空啦", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改密码出错：用户名没查着", null);
        }
        String newPassword = object.getString("newPassword");
        if (!newPassword.equals(object.getString("newPassword1"))) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改密码出错：新密码两次不一致", null);
        }
        try {
            userService.updateUserPassword(username, object.getString("oldPassword"), object.getString("newPassword"));
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "修改密码异常", null);
        }

    }


    @PostMapping("updateUserHeadImg")
    public RestResultVo updateUserHeadImg(@RequestBody JSONObject object) {
        String username = object.getString("username");
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改头像出错：用户名空啦", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改头像出错：用户名没查着", null);
        }
        String imgUrl = object.getString("imgUrl");
        if (StringUtils.isEmpty(imgUrl)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改头像出错：头像地址空了", null);
        }
        try {
            userService.updateUserHeadImg(username, imgUrl);
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
            redisDao.set(username, JSON.toJSONString(user));
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "修改头像异常", null);
        }

    }

    @GetMapping("updateUserStatus")
    public RestResultVo updateUserStatus(@RequestParam("operator") String operator,@RequestParam("username") String username,@RequestParam("status") String status) {
        if (StringUtils.isEmpty(operator)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "参数有误，无权操作", null);
        }
        User user = userService.redisGetUser(operator);
        if (StringUtils.isEmpty(user) || !Role.ADMIN.getValue().equals(user.getAuthorities().get(0))) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权操作", null);
        }
        try {
            userService.updateUserStatus(username,status);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", user);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "操作异常", null);
        }
    }

    /**
     * 保存
     *
     * @param articleVo
     * @return
     */
    @PostMapping("saveNewArticle")
    public RestResultVo saveNewArticle(@RequestBody ArticleVo articleVo) {
        if (!"1".equals(articleVo.validateParams())) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, articleVo.validateParams(), null);
        }
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, articleVo.getUsername());
        Object code = redisDao.get(key);
        redisDao.del(key);
        if (code == null || ((int) code) != Integer.parseInt(articleVo.getVercode())) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }
        try {
            String serialNumber = userService.saveNewArticle(articleVo);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", serialNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResultVo(RestResultVo.RestResultCode.EXCEPTION, "保存信息异常", null);
        }
    }

    @GetMapping("queryEditArticle")
    public RestResultVo saveNewArticle(@RequestParam("id") String id) {
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", catalogueService.queryConnotationDetail(id));
    }

    @PostMapping("updateArticle")
    public RestResultVo updateArticle(@RequestBody JSONObject obj) {
        String serialNumber = obj.getString("serialNumber");
        String type = obj.getString("type");
        String content = obj.getString("content");
        String title = obj.getString("title");
        String vercode = obj.getString("vercode");
        String username = obj.getString("author");
        if (StringUtils.isEmpty(serialNumber)
                || StringUtils.isEmpty(type)
                || StringUtils.isEmpty(content)
                || StringUtils.isEmpty(title)
                || StringUtils.isEmpty(vercode)
                || StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改失败：参数为空", null);
        }
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, username);
        Object code = redisDao.get(key);
        redisDao.del(key);
        if (code == null || ((int) code) != Integer.parseInt(vercode)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }
        try {
            userService.updateArticle(serialNumber, type, content, title);
        } catch (Exception e) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "修改异常：" + e.getMessage(), null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);

    }

    @PostMapping("/saveComment")
    public RestResultVo saveComment(@RequestBody Comment comment) {
        mongoService.saveComment(comment);
        catalogueService.updateCommentTimes(comment.getSerialNumber());
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.queryCommentBySerialNumber(comment.getSerialNumber()));
    }

    @GetMapping("/delComment")
    public RestResultVo delComment(@RequestParam("id") String id, @RequestParam("serialNumber") String serialNumber) {
        List<Comment> list = null;
        if (mongoService.delComment(id).getDeletedCount() == 1) {
            list = mongoService.queryCommentBySerialNumber(serialNumber);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", list);
    }

    @GetMapping("/dealArticle")
    public RestResultVo dealArticle(@RequestParam("id") String id, @RequestParam("type") String type, @RequestParam("val") boolean val) {
        if ("del".equals(type)) {
            catalogueService.deleteCatalogueDetail(id);
            catalogueService.deleteConnotation(id);
        }
        if ("best".equals(type)) {
            catalogueService.updateCatalogueBest(id, val);
        }
        if ("stick".equals(type)) {
            catalogueService.updateCatalogueStick(id, val);
        }
        if ("publicity".equals(type)) {
            catalogueService.updateCataloguePublicity(id, val);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
    }


    @GetMapping("/queryGuestReply")
    public RestResultVo queryGuestReply(@RequestParam("username") String username) {
        if (StringUtils.isEmpty(username)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权查看", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user) || !Role.ADMIN.getValue().equals(user.getAuthorities().get(0))) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权查看", null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.queryGuestReplyList(null));
    }


    @PostMapping("/updateGuestReplyStatus")
    public RestResultVo updateGuestReplyStatus(@RequestBody JSONObject object) {
        String username = object.getString("username");
        String id = object.getString("id");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(id)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权操作", null);
        }
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user) || !Role.ADMIN.getValue().equals(user.getAuthorities().get(0))) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "抱歉您无权操作", null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", mongoService.updateGuestReplyStatus(id));
    }

    /**
     * 网站日志更新
     * @param updateBlog
     * @return
     */
    @PostMapping("saveUpdateBlogs")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public RestResultVo saveUpdateBlogs(@RequestBody UpdateBlog updateBlog){
        if(!"1".equals(updateBlog.validate())){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, updateBlog.validate(), null);
        }
        mongoService.saveUpdateBlog(updateBlog);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
    }
    /*****************通用工具类**************************/

    @GetMapping("queryMyArticle")
    //@PreAuthorize("hasAnyAuthority('ADMIN')")
    //@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize
    public RestResultVo queryMyArticle(@RequestParam("username") String username) {
        /*final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();*/

        //return favoriteService.getFavoritesByUsername(pageIndex, pageSize, username);
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户未登陆或无此用户", null);
        }
        JSONObject resObj = new JSONObject();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("username",username);
        resObj.put("myCatalogue",catalogueService.queryCatalogueByParams(queryParams));
        resObj.put("statistics",catalogueService.countCatalogueAuthor(username,null));
        //resObj.put("myCollection",favoriteService.getFavoritesByUsername(username));
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",resObj);
    }

    @GetMapping("queryMyCollection")
    public RestResultVo queryMyCollection(@RequestParam("username") String username) {
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户未登陆或无此用户", null);
        }
        JSONObject resObj = new JSONObject();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("username",username);
        catalogueService.countCatalogueAuthor(username,null);
        resObj.put("statistics",catalogueService.countCatalogueAuthor(username,null));
        resObj.put("myCollection",favoriteService.getFavoritesByUsername(username));
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",resObj);
    }

    //@DeleteMapping("favorite")
    @GetMapping("delFavorites")
    //@PreAuthorize("hasAnyAuthority('USER')")
    public RestResultVo deleteFavorites(@RequestParam("id") String id) {
        try {
            favoriteService.delFavorite(id);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
        }catch (Exception e){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, e.getMessage(), null);
        }
    }

    @PostMapping("saveFavorite")
    //@PreAuthorize("hasAnyAuthority('USER')")
    public RestResultVo addFavorites(@RequestBody Favorite favorites) {
        if(!"1".equals(favorites.validate())){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, favorites.validate(), null);
        }
        Favorite oldFavorite = favoriteService.queryFavorite(favorites.getSerialNumber(),favorites.getUsername());
        if(oldFavorite != null){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED,"您已收藏！请勿重复操作", null);
        }
        try {
            favoriteService.saveFavorite(favorites);
            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
        }catch (Exception e){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, e.getMessage(), null);
        }
    }

    @GetMapping("queryUserSelfReciveMessage")
    public RestResultVo queryUserSelfReciveMessage(@RequestParam("username") String username){
        User user = userService.redisGetUser(username);
        if (StringUtils.isEmpty(user)) {
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户未登陆或无此用户", null);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "",  userService.queryUserCommentsList(null,username));
    }
}
