package org.awesome.service;

import org.awesome.models.User;
import org.awesome.vo.ArticleVo;
import org.awesome.vo.RestResultVo;

import java.util.List;

public interface IUserService {

    User findUserByName(String username);

    List<String> findUserAuthoritiesByName(String username);

    void saveNewArticle(ArticleVo articleVo) throws Exception;


}
