package org.awesome.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.awesome.Dao.RedisDao;
import org.awesome.mapper.CatalogueMapper;
import org.awesome.mapper.ConnotationMapper;
import org.awesome.models.Catalogue;
import org.awesome.models.OperationFlow;
import org.awesome.service.ICatalogueService;
import org.awesome.vo.CatalogueVo;
import org.awesome.vo.RestResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CatalogueService implements ICatalogueService {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogueService.class);

    @Resource
    private CatalogueMapper catalogueMapper;
    @Resource
    private ConnotationMapper connotationMapper;


    @Override
    public List<CatalogueVo> queryCatalogue(Map<String,String> params){
        LOG.info("queryCatalogue查询参数：[{}]",params);
        return catalogueMapper.queryFirstPage(params);
    }

    @Override
    public  CatalogueVo queryConnotationDetail(String serialNumber){
        LOG.info("queryConnotationDetail查询参数：[{}]",serialNumber);
        return catalogueMapper.queryConnotationDetail(serialNumber);
    }
    @Override
    public List<Catalogue> queryCatalogueByParams(Map<String,String> params){
        LOG.info("queryCatalogueByParams查询参数：[{}]",params);
        return catalogueMapper.queryCatalogueByParams(params);
    }

    @Override
    public int updateSeenTimes(String serialNumber){
        return catalogueMapper.updateSeenTimes(serialNumber);
    }

    @Override
    public int updateCommentTimes(String serialNumber){
        return catalogueMapper.updateCommentTimes(serialNumber);
    }

    @Override
    public int deleteCatalogueDetail(String serialNumber){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("serial_number",serialNumber);
        return catalogueMapper.deleteByMap(map);
    }
    @Override
    public int deleteConnotation(String serialNumber){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("serial_number",serialNumber);
        return connotationMapper.deleteByMap(map);
    }

    @Override
    public int updateCatalogueStick(String serialNumber,boolean stick){
        return catalogueMapper.updateCatalogueStick(serialNumber,stick);
    }

    @Override
    public int updateCatalogueBest(String serialNumber,boolean best){
        return catalogueMapper.updateCatalogueBest(serialNumber,best);
    }

    @Override
    public int updateCataloguePublicity(String serialNumber,boolean publicity){
        return catalogueMapper.updateCataloguePublicity(serialNumber,publicity);
    }
    @Override
    public Catalogue queryCatalogueBySerialNumber(String serialNumber){
        return catalogueMapper.queryCatalogueBySerialNumber(serialNumber);
    }

    @Override
    public List<JSONObject> countCatalogueAuthor(String username,String publicity){
        return catalogueMapper.countCatalogueAuthor(username,publicity);
    }
}
