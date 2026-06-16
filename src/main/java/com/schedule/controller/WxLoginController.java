package com.schedule.controller;

import com.schedule.entity.User;
import com.schedule.entity.UserToken;
import com.schedule.mapper.UserTokenMapper;
import com.schedule.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api") // 关键：添加 /api 前缀，匹配前端请求路径
public class WxLoginController {

    private final UserService userService;
    private final UserTokenMapper userTokenMapper;

    public WxLoginController(UserService userService, UserTokenMapper userTokenMapper) {
        this.userService = userService;
        this.userTokenMapper = userTokenMapper;
    }

    @PostMapping("/wxlogin")
    public Map<String, Object> wxLogin(@RequestParam String code) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用微信登录业务逻辑
            User user = userService.wxLogin(code);

            // 生成并保存 token
            String token = UUID.randomUUID().toString();
            UserToken userToken = new UserToken();
            userToken.setUserId(user.getId());
            userToken.setToken(token);
            userTokenMapper.insert(userToken);

            // 登录成功返回
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token);

        } catch (Exception e) {
            // 登录失败返回
            result.put("code", 500);
            result.put("msg", "登录失败：" + e.getMessage());
            result.put("token", null);
        }

        return result;
    }
}