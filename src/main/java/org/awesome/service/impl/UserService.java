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
import org.awesome.vo.RestResultVo;
import org.awesome.vo.UserBasicInfoVo;
import org.awesome.vo.UserCommentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

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
        if(catalogueMapper.updateCatalogueTitle(serialNumber, type, title) != 1
                || connotationMapper.updateConnotation(serialNumber, content) != 1){
            throw new Exception("update article fail");
        }
    }

    @Override
    public void updateUserBasicInfo(UserBasicInfoVo userBasicInfoVo) throws Exception{
        if(userMapper.updateUserBasicInfo(userBasicInfoVo) !=1){
            throw new Exception("update user basicInfo fail");

        }
    }

    @Override
    public void updateUserPassword(String username,String oldPassword,String newPassword)throws Exception{
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, oldPassword);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
            userMapper.updateUserPassword(username,bCryptPasswordEncoder.encode(newPassword));
        } catch (Exception e) {
            LOG.error("Authenticate failed [{}]", username);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public void updateUserHeadImg(String username, String imgUrl)throws Exception{
        if(userMapper.updateUserHeadImg(username,imgUrl) !=1){
            throw new Exception("update user headPortraitUrl fail");
        }
    }

    @Override
    public List<UserCommentVo> queryUserCommentsList(String username,String defendant){
        List<Comment> ownCommentList = null;
        if(!StringUtils.isEmpty(username)){
            ownCommentList = mongoService.queryUserCommentInfo(username);
        }else
        if(!StringUtils.isEmpty(defendant)){
            ownCommentList = mongoService.queryCommentByDefendant(defendant);
        }
        List<UserCommentVo> ownCommentInfoList = new ArrayList<UserCommentVo>();
        if(ownCommentList != null){
            for (Comment comment:ownCommentList) {
                Catalogue catalogue =catalogueMapper.queryCatalogueBySerialNumber(comment.getSerialNumber());
                if(catalogue == null){
                    continue;
                }
                ownCommentInfoList.add(new UserCommentVo(comment.getSerialNumber(),
                        catalogue.getTitle(),
                        comment.getUsername(),
                        comment.getReplyContent(),
                        comment.getCreateTime(),
                        comment.getAuthor(),
                        comment.getAuthorHeadUrl())
                );
            }
        }
        return ownCommentInfoList;
    }
}
