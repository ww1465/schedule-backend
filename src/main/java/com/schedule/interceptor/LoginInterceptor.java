package com.schedule.interceptor;

import com.schedule.entity.User;
import com.schedule.entity.UserToken;
import com.schedule.mapper.UserMapper;
import com.schedule.mapper.UserTokenMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    private final UserTokenMapper userTokenMapper;
    private final UserMapper userMapper;

    public LoginInterceptor(UserTokenMapper userTokenMapper, UserMapper userMapper) {
        this.userTokenMapper = userTokenMapper;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 精确匹配登录接口，防止误放行如 /api/schedule/wxlogin 等路径
        if ("/api/wxlogin".equals(uri)) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
            return false;
        }

        UserToken userToken = userTokenMapper.selectByToken(token);
        if (userToken == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token无效，请重新登录\"}");
            return false;
        }

        User loginUser = userMapper.selectById(userToken.getUserId());
        request.setAttribute("loginUser", loginUser);
        return true;
    }
}