package org.awesome.filter;

import org.awesome.Dao.RedisDao;
import org.awesome.constants.Constant;
import org.awesome.security.JwtUserDetailService;
import org.awesome.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * token校验过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailService jwtUserDetailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisDao redisDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(Constant.JWT_HEADER);
        if (header != null && header.startsWith(Constant.JWT_PREFIX)) {
            String token = header.replace(Constant.JWT_PREFIX, "");
            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = jwtUserDetailService.loadUserByUsername(username);
                boolean isValidate = jwtTokenUtil.validateToken(token, userDetails);
                boolean isExpired = jwtTokenUtil.isExpired(token);
                boolean canPass = false;

                if (isValidate && !isExpired) {
                    canPass = true;

                    //token一分钟之内要过期，刷新token
                    if (jwtTokenUtil.isExpireSoon(token)) {
                        String newToken = jwtTokenUtil.refreshToken(token);

                        //旧token放入黑名单,保留一分钟，解决并发过程中新token刷新同时，旧token请求失效的情况
                        redisDao.set(token, newToken, Constant.REDIS_TOKEN_TIMEOUT);

                        //response中返回新token
                        response.setHeader(Constant.JWT_HEADER, newToken);
                    }
                } else if (isValidate) {
                    //如果token在黑名单中，说明该token已经被并发请求刷新，并且已经返回了新token，但该token一分钟之内就会失效
                    Object newToken = redisDao.get(token);
                    if (!Objects.isNull(newToken)) {
                        canPass = true;

                        //response中返回新token
                        response.setHeader(Constant.JWT_HEADER, Constant.JWT_PREFIX + newToken);
                    }
                }

                if (canPass) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
