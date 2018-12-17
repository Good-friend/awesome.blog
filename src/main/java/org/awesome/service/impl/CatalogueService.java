package org.awesome.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.awesome.mapper.CatalogueMapper;
import org.awesome.models.Catalogue;
import org.awesome.service.ICatalogueService;
import org.awesome.vo.CatalogueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CatalogueService implements ICatalogueService {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogueService.class);

    @Resource
    private CatalogueMapper catalogueMapper;

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
}
