package org.awesome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.awesome.mapper.AuthorityMapper;
import org.awesome.mapper.UserMapper;
import org.awesome.models.Authority;
import org.awesome.models.User;
import org.awesome.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

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
}
