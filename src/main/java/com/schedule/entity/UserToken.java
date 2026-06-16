package com.schedule.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserToken {
    private Long id;
    private Long userId;
    private String token;
    private Date createTime;
}