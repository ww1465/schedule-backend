package com.schedule.service;

import com.schedule.entity.User;

public interface UserService {
    User wxLogin(String code);
}