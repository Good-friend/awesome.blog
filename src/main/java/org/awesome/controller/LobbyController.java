package org.awesome.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.glassfish.gmbal.ParameterNames;
import org.awesome.models.Catalogue;
import org.awesome.service.ICatalogueService;
import org.awesome.vo.CatalogueVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/lobby/")
public class LobbyController {

    @Resource
    private ICatalogueService catalogueService;

    @GetMapping("queryCatalogueList")
    public JSONObject queryCatalogueList(HttpServletRequest request) {
        JSONObject resObj = new JSONObject();
        Map<String,String> queryParams = new HashMap<String,String>();
        String status = request.getParameter("status");
        if(status != null){
            queryParams.put("status",status);
        }
        String best = request.getParameter("best");
        if(best != null){
            queryParams.put("best",best);
        }
        String type = request.getParameter("type");
        if(type != null){
            queryParams.put("type",type);
        }
        String orderType = request.getParameter("orderType");
        if(orderType != null){
            queryParams.put("orderType",orderType);
        }
        resObj.put("catalogueList",catalogueService.queryCatalogue(queryParams));
        return resObj;

    }
    @GetMapping("queryConnotationDetail")
    public CatalogueVo queryConnotationDetail(@RequestParam("serialNumber") String serialNumber){
        return catalogueService.queryConnotationDetail(serialNumber);
    }
}
