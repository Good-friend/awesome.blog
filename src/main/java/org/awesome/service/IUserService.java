package org.awesome.service;

import org.awesome.models.OperationFlow;
import org.awesome.models.User;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.RestResultVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IUserService {

    User findUserByName(String username);

    User redisGetUser(String username);

    List<String> findUserAuthoritiesByName(String username);

    String saveNewArticle(ArticleVo articleVo) throws Exception;

    OperationFlow createOperationFlow(HttpServletRequest request, String action);

    void updateArticle(String serialNumber,String type,String content,String title) throws Exception;

}
