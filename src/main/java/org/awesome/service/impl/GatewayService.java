package org.awesome.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.awesome.utils.CommonUtils;
import org.awesome.utils.EmailUtils;
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

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private UserService userService;


    @Override
    public RestResultVo login(String username, String password, int identityCode, HttpServletRequest request) {
        LOG.info("[{}] attempt login", username);

        //校验验证码
        String key = MessageFormat.format(Constant.REDIS_IDENTIFYCODE_KEY_WRAPPER, CommonUtils.getIpAddress(request));

        Object code = redisDao.get(key);

        if (code == null || ((int) code) != identityCode) {
            redisDao.del(key);
            LOG.error("The identifyCode invalid [{}] provided.", username);
            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "人类验证错误", null);
        }

        redisDao.del(key);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            LOG.error("Authenticate failed [{}]", username);

            return new RestResultVo(RestResultVo.RestResultCode.FAILED, "用户名或密码错误", null);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);

        LOG.info("[{}] login successfully.", username);
        User user = userService.findUserByName(username);
        user.setPassword("");
        user.setAuthorities(userService.findUserAuthoritiesByName(username));
        redisDao.set(username, JSON.toJSONString(user),Constant.JWT_TIMEOUT);
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

    @Override
    public void registerSendEmail(String nickname,String username,String password,String email){
        System.out.println("初始化邮件发送.......");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("邮件开始发送！");
                String content = "尊敬的" + nickname + "：" + System.getProperty("line.separator") +
                        "      您好，非常感谢您加入【葛耀的小站】（地址：http://www.geyaoln.xin），成为我们中的一员。以后请多多指教。" + System.getProperty("line.separator") +
                        "      以下是您的会员信息：" + System.getProperty("line.separator") +
                        "      登录名：" + username + System.getProperty("line.separator") +
                        "      密码：" + password + System.getProperty("line.separator") +
                        "    您可以登陆【葛耀的小站】我的信息里修改密码";
                LOG.info("邮件内容：[{}]", content);
                emailUtils.sendBy163(content, "【葛耀的小站】会员信息", email);
                System.out.println("邮件发送成功！");
            }
        }).start();
    }
}
