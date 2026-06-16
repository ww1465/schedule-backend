package com.schedule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    private String appId;
    private String appSecret;
    private String code2sessionUrl;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public String getCode2sessionUrl() { return code2sessionUrl; }
    public void setCode2sessionUrl(String code2sessionUrl) { this.code2sessionUrl = code2sessionUrl; }
}