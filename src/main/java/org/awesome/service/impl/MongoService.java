package org.awesome.service.impl;

import org.awesome.Dao.RedisDao;
import org.awesome.models.UpdateBlog;
import org.awesome.service.IMongoService;
import org.awesome.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MongoService implements IMongoService {

    private static final Logger LOG = LoggerFactory.getLogger(MongoService.class);

    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RedisDao redisDao;


    @Override
    public List<UpdateBlog> queryUpdateBlogList(){
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC,"version"));
        return mongoTemplate.find(query,UpdateBlog.class);
    }

    @Override
    public void saveUpdateBlog(UpdateBlog updateBlog){
        updateBlog.setUpdateDate(CommonUtils.getNowDate());
        updateBlog.setVersion(getVersion());
        mongoTemplate.save(updateBlog);
    }
    private String getVersion(){
        String version = (String)redisDao.get("version");
        if(version == null){
            version = String.valueOf(0.00);
        }else{
            version = String.valueOf(Double.parseDouble(version)+0.01);
        }
        redisDao.set("version",version);
        return version;
    }

}
