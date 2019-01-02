package org.awesome.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.awesome.Dao.RedisDao;
import org.awesome.mapper.AuthorityMapper;
import org.awesome.mapper.CatalogueMapper;
import org.awesome.mapper.ConnotationMapper;
import org.awesome.mapper.UserMapper;
import org.awesome.models.*;
import org.awesome.service.IUserService;
import org.awesome.utils.CommonUtils;
import org.awesome.vo.ArticleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements IUserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private CatalogueMapper catalogueMapper;

    @Resource
    private ConnotationMapper connotationMapper;

    @Resource
    private RedisDao redisDao;
    @Resource
    private MongoService mongoService;

    @Override
    public User findUserByName(String username) {
        LOG.info("Attempt find user [{}] ", username);

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            LOG.error("User not found [{}]", username);
            throw new UsernameNotFoundException(String.format("User not found with username '%s'", username));
        }
        List<String> authorities = findUserAuthoritiesByName(username);
        user.setAuthorities(authorities);

        LOG.info("User has been found [{}]", username);

        return user;
    }

    @Override
    public List<String> findUserAuthoritiesByName(String username) {
        LOG.info("Attempt find user's authorities [{}] ", username);

        List<Authority> authorities = authorityMapper.selectList(new QueryWrapper<Authority>().eq("username", username));
        List<String> result = new ArrayList<>();
        for (Authority authority : authorities) {
            result.add(authority.getAuthority());
        }

        LOG.info("User's authorities have been found [{}] ", username);

        return result;
    }

    @Override
    public String saveNewArticle(ArticleVo articleVo) throws Exception{
        String serialNumber = getMajorKeyId(articleVo.getType().substring(0,1));
        Catalogue catalogue = new Catalogue();
        catalogue.setSerialNumber(serialNumber);
        catalogue.setTitle(articleVo.getTitle());
        catalogue.setStatus("0");
        catalogue.setCreateTime(CommonUtils.getNowTime());
        catalogue.setEndTime(articleVo.getEndTime());
        catalogue.setSeenTimes(0);
        catalogue.setCommentTimes(0);
        catalogue.setBest(articleVo.isBest());
        catalogue.setAuthor(articleVo.getUsername());
        catalogue.setType(articleVo.getType());
        catalogue.setStick(articleVo.isStick());
        catalogue.setPublicity(articleVo.isPublicity());
        catalogueMapper.insert(catalogue);
        Connotation connotation= new Connotation();
        connotation.setSerialNumber(serialNumber);
        connotation.setContent(articleVo.getContent());
        connotationMapper.insert(connotation);
        return serialNumber;
    }

    private static String str = "0000";
    private String getMajorKeyId(String type){
        String id = (String)redisDao.get("major_key_id");
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        String sysDate = time.format(nowTime);
        int p = Integer.parseInt(str) + 1;
        if(p > 9999) {
            p = 0;
        }
        str = String.format("%04d",p);
        id = type+sysDate+str;
        redisDao.set("major_key_id",id);
        return id;
    }

    @Override
    public OperationFlow createOperationFlow(HttpServletRequest request, String action){
        String serialNumber = request.getParameter("serialNumber");
        if(serialNumber == null){
            LOG.error("serialNumber is null !");
            return null;
        }
        String username = request.getParameter("username");
        String ip = CommonUtils.getIpAddress(request);
        List<OperationFlow> thumbUpList = mongoService.queryOperationFlow(action,serialNumber,username,ip);
        if(thumbUpList == null || thumbUpList.size() <1){
            OperationFlow operationFlow = new OperationFlow();
            operationFlow.setAction(action);
            operationFlow.setTarget(serialNumber);
            operationFlow.setWho(username);
            operationFlow.setIp(ip);
            return operationFlow;
        }else{
            return null;
        }
    }
    @Override
    public  User redisGetUser(String username){
        Object userObj = redisDao.get(username);
        if(userObj == null) {
            return null;
        }
        return JSON.parseObject((String)userObj,User.class);
    }

    @Override
    public void updateArticle(String serialNumber,String type,String content,String title) throws Exception{
       catalogueMapper.updateCatalogueTitle(serialNumber, type, title);
       connotationMapper.updateConnotation(serialNumber, content);
    }
}
