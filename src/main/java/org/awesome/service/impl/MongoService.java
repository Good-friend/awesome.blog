package org.awesome.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.awesome.Dao.RedisDao;
import org.awesome.models.Comment;
import org.awesome.models.GuestReply;
import org.awesome.models.OperationFlow;
import org.awesome.models.UpdateBlog;
import org.awesome.service.IMongoService;
import org.awesome.utils.CommonUtils;
import org.awesome.vo.UserCommentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        query.with(new Sort(Sort.Direction.DESC,"updateDate"));
        return mongoTemplate.find(query,UpdateBlog.class);
    }

    @Override
    public void saveUpdateBlog(UpdateBlog updateBlog){
        updateBlog.setUpdateDate(CommonUtils.getNowDate());
        mongoTemplate.save(updateBlog);
    }

    @Override
    public void saveOperationFlow(OperationFlow operationFlow){
        operationFlow.setTime(CommonUtils.getNowTime());
        mongoTemplate.save(operationFlow);
    }

    @Override
    public List<Comment> queryCommentBySerialNumber(String serialNumber){
        Query query = new Query(Criteria.where("serialNumber").is(serialNumber));
        query.with(new Sort(Sort.Direction.DESC,"createTime"));
        return mongoTemplate.find(query,Comment.class);
    }

    @Override
    public void saveComment(Comment comment){
        comment.setId(CommonUtils.getMajorKeyId("com"));
        comment.setCreateTime(CommonUtils.getNowTime());
        mongoTemplate.save(comment);
    }

    @Override
    public UpdateResult updateThumbUp(String id){
        return mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(id)),
                Update.update("thumbUpTimes", mongoTemplate.findById(id,Comment.class).getThumbUpTimes()+1),
                Comment.class);
    }


    @Override
    public List<OperationFlow> queryOperationFlow(String action,String target,String who,String ip){
        LOG.info("queryOperationFlow查询参数："+action+"；"+target+"；"+who+"；"+ip);
        Query query = new Query(
                Criteria.where("action").is(action).and("target").is(target));
        if(!StringUtils.isEmpty(who)){
            query.addCriteria(Criteria.where("who").is(who));
        }
        if(!StringUtils.isEmpty(ip)) {
            query.addCriteria(Criteria.where("ip").is(ip));
        }
        return mongoTemplate.find(query,OperationFlow.class);
    }

    @Override
    public DeleteResult delComment(String id){
         return mongoTemplate.remove(new Query(Criteria.where("_id").is(id)),Comment.class);
    }

    @Override
    public List<Comment> queryUserCommentInfo(String username){
        Query query = new Query(Criteria.where("username").is(username));
        query.with(new Sort(Sort.Direction.DESC,"createTime"));
        return mongoTemplate.find(query,Comment.class);
    }


    @Override
    public void saveGuestReply(GuestReply guestReply){
        guestReply.setCreateTime(CommonUtils.getNowTime());
        mongoTemplate.save(guestReply);
    }

    @Override
    public List<GuestReply> queryGuestReplyList(String dealStatus){
        Query query = new Query();
        if(!StringUtils.isEmpty(dealStatus)){
            query.addCriteria(Criteria.where("dealStatus").is(dealStatus));
        }
        query.with(new Sort(Sort.Direction.ASC,"dealStatus"));
        query.with(new Sort(Sort.Direction.DESC,"createTime"));
        return mongoTemplate.find(query,GuestReply.class);
    }

    @Override
    public UpdateResult updateGuestReplyStatus(String id){
        return mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)),
                Update.update("dealStatus", "1"),
                GuestReply.class);
    }

    @Override
    public List<Comment> queryCommentByDefendant(String defendant){
        Query query = new Query(Criteria.where("defendant").is(defendant));
        query.with(new Sort(Sort.Direction.DESC,"createTime"));
        return mongoTemplate.find(query,Comment.class);
    }
}
