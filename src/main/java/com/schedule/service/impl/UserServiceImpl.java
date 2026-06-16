package com.schedule.service.impl;

import com.schedule.entity.User;
import com.schedule.mapper.UserMapper;
import com.schedule.service.UserService;
import com.schedule.config.WxProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WxProperties wxProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public UserServiceImpl(UserMapper userMapper, WxProperties wxProperties) {
        this.userMapper = userMapper;
        this.wxProperties = wxProperties;
    }

    @Override
    public User wxLogin(String code) {
        String url = wxProperties.getCode2sessionUrl()
                + "?appid=" + wxProperties.getAppId()
                + "&secret=" + wxProperties.getAppSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        try {
            String response = restTemplate.getForObject(url, String.class);
            Map<String, Object> res = objectMapper.readValue(response, Map.class);

            String openid = (String) res.get("openid");
            if (openid == null) {
                System.out.println("微信返回内容：" + response);
                throw new RuntimeException("未获取到openid");
            }

            User user = userMapper.selectByOpenid(openid);
            if (user == null) {
                user = new User();
                user.setOpenid(openid);
                user.setNickname("微信用户");
                user.setCreateTime(new Date());
                userMapper.insert(user);
            }
            return user;

        } catch (Exception e) {
            throw new RuntimeException("调用微信接口异常", e);
        }
    }
}