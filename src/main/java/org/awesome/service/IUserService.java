package org.awesome.service;

import org.awesome.models.OperationFlow;
import org.awesome.models.User;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.RestResultVo;
import org.awesome.vo.UserBasicInfoVo;
import org.awesome.vo.UserCommentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserService {

    User findUserByName(String username);

    List<User> findAllUser();

    User redisGetUser(String username);

    List<String> findUserAuthoritiesByName(String username);

    String saveNewArticle(ArticleVo articleVo) throws Exception;

    OperationFlow createOperationFlow(HttpServletRequest request, String action);

    void updateArticle(String serialNumber,String type,String content,String title,String contentOriginal) throws Exception;

    void updateUserBasicInfo(UserBasicInfoVo userBasicInfoVo)throws Exception;

    void updateUserPassword(String username,String oldPassword,String newPassword)throws Exception;

    void updateUserHeadImg(String username, String imgUrl)throws Exception;

    void updateUserStatus(String username, String status)throws Exception;

    List<UserCommentVo> queryUserCommentsList(String username,String defendant);
}
