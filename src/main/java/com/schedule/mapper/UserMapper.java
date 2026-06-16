package com.schedule.mapper;

import com.schedule.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User selectByOpenid(String openid);

    @Insert("INSERT INTO user(openid, nickname, create_time) VALUES(#{openid}, #{nickname}, #{createTime})")
    int insert(User user);
}