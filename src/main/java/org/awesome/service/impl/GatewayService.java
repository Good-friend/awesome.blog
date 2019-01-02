package org.awesome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.awesome.Dao.RedisDao;
import org.awesome.constants.Constant;
import org.awesome.enums.Role;
import org.awesome.mapper.AuthorityMapper;
import org.awesome.mapper.UserMapper;
import org.awesome.models.Authority;
import org.awesome.models.User;
import org.awesome.security.JwtUserDetailService;
import org.awesome.service.IGatewayService;
import org.awesome.utils.JwtTokenUtil;
import org.awesome.vo.RestResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class GatewayService implements IGatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private JwtUserDetailService jwtUserDetailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisDao redisDao;


    @Override
    public RestResultVo login(String username, String password, int identityCode) {
        LOG.info("[{}] attempt login", username);

        //校验验证码
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, username);

        Object code = redisDao.get(key);

        if (code == null || ((int) code) != identityCode) {
            redisDao.del(key);
            LOG.error("The identifyCode invalid [{}] provided.", username);
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "identifyCode invalid.", null);
        }

        redisDao.del(key);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            LOG.error("Authenticate failed [{}]", username);

            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "Authenticate failed.", null);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);

        LOG.info("[{}] login successfully.", username);

        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, jwtTokenUtil.generateToken(userDetails));
    }

    @Override
    public RestResultVo register(User user) {
        LOG.info("[{}] attempt register.", user.getUsername());

        String username = user.getUsername();
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", username)) != null) {

            LOG.error("[{}] already exists..", user.getUsername());

            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "user already exists.", null);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userMapper.insert(user);

        Authority authority = new Authority();
        authority.setAuthority(Role.USER.getValue());
        authority.setUsername(username);
        authorityMapper.insert(authority);

        LOG.info("[{}] register successfully.", user.getUsername());

        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, "register success.", null);
    }

    @Override
    public RestResultVo refreshToken(String oldToken) {
        LOG.info("refresh token [{}]", oldToken);

        String token = oldToken.replace("Bearer ", "");
        if (!jwtTokenUtil.isExpired(token)) {

            LOG.info("refresh token successfully [{}]", oldToken);

            return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, jwtTokenUtil.refreshToken(token));
        }

        LOG.error("refresh token failed [{}]", oldToken);

        return new RestResultVo(RestResultVo.RestResultCode.FAILED, "token is expired. Login again, please.", null);
    }
}
