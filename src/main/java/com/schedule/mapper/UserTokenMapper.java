package com.schedule.mapper;

import com.schedule.entity.UserToken;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserTokenMapper {

    @Insert("INSERT INTO user_token(user_id, token) VALUES(#{userId}, #{token})")
    int insert(UserToken token);

    @Select("SELECT * FROM user_token WHERE token = #{token}")
    UserToken selectByToken(String token);
}