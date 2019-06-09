package org.awesome.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import org.awesome.Dao.RedisDao;
import org.awesome.models.*;
import org.awesome.service.ICatalogueService;
import org.awesome.service.impl.MongoService;
import org.awesome.service.impl.UserService;
import org.awesome.utils.CommonUtils;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.CatalogueVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.UserCommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/lobby/")
public class LobbyController {

    @Autowired
    private ICatalogueService catalogueService;
    @Resource
    private MongoService mongoService;
    @Resource
    private RedisDao redisDao;
    @Resource
    private UserService userService;

    @GetMapping("queryCatalogueList")
    public RestResultVo queryCatalogueList(HttpServletRequest request) {
        JSONObject resObj = new JSONObject();
        Map<String, String> queryParams = new HashMap<String, String>();
        String orderType = request.getParameter("orderType");
        if (orderType == null) {
            orderType = "id";
        }
        queryParams.put("orderType", orderType);
        String queryCountStr = request.getParameter("queryCountStr");
        if (queryCountStr != null) {
            queryParams.put("queryCount", queryCountStr);
        }
        String stick = request.getParameter("stick");
        if (stick != null) {
            queryParams.put("stick", "0");
        } else {//查询置顶
            Map<String, String> stickParams = new HashMap<String, String>();
            stickParams.put("orderType", "id");
            stickParams.put("stick", "1");
            resObj.put("stickList", catalogueService.queryCatalogue(stickParams));
        }
        resObj.put("catalogueList", catalogueService.queryCatalogue(queryParams));
        resObj.put("countByAuthor", catalogueService.countCatalogueAuthor(null,null));
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", resObj);

    }

    @GetMapping("queryConnotationDetail")
    public RestResultVo queryConnotationDetail(@RequestParam("serialNumber") String serialNumber,HttpServletRequest request) {
        OperationFlow operationFlow = userService.createOperationFlow(request,"1");
        if(operationFlow != null && catalogueService.updateSeenTimes(serialNumber) ==1){
            mongoService.saveOperationFlow(operationFlow);
        }
        CatalogueVo catalogueVo = catalogueService.queryConnotationDetail(serialNumber);
        JSONObject resObj = new JSONObject();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("orderType","heat");
        resObj.put("heatData",catalogueService.queryCatalogueByParams(queryParams));
        resObj.put("detailData",catalogueVo);
        resObj.put("commentList",mongoService.queryCommentBySerialNumber(serialNumber));
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", resObj);
    }


    /**
     * 自己的帖
     * @param username
     * @return
     */
    @GetMapping("/getOwnCatalogue")
    public RestResultVo getOwnCatalogue(@RequestParam("username") String username){
        JSONObject resObj = new JSONObject();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("username",username);
        User user = userService.redisGetUser(username);
        String publicity = null;
        if(user == null){
            publicity = "1";
            queryParams.put("lookOther","true");
            user = userService.findUserByName(username);
            user.setPassword("");
            user.setAuthorities(userService.findUserAuthoritiesByName(username));
        }
        resObj.put("user",user);
        resObj.put("ownCatalogue",catalogueService.queryCatalogueByParams(queryParams));

        resObj.put("ownCommentInfoList",userService.queryUserCommentsList(username,null));
        List<JSONObject> countByAuthorList = catalogueService.countCatalogueAuthor(username,publicity);
        resObj.put("countByAuthor",(countByAuthorList == null||countByAuthorList.size() < 1)?null:countByAuthorList.get(0));
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", resObj);
    }

    @GetMapping("/updateThumbUp")
    public RestResultVo updateThumbUp(HttpServletRequest request){
        OperationFlow operationFlow = userService.createOperationFlow(request,"3");
        if(operationFlow == null){
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "您已点过赞", null);
        }
        String id = request.getParameter("id");
        UpdateResult result = mongoService.updateThumbUp(id);
        if(result.isModifiedCountAvailable()){
            mongoService.saveOperationFlow(operationFlow);
        }
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", result);
    }

    @PostMapping("/saveGuestReply")
    public RestResultVo saveGuestReply(@RequestBody GuestReply guestReply){
        mongoService.saveGuestReply(guestReply);
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "", null);
    }

}
