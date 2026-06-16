package com.schedule.mapper;

import com.schedule.entity.PriorityConfig;
import org.apache.ibatis.annotations.Param;

public interface PriorityConfigMapper {
    PriorityConfig selectByUserId(@Param("userId") Long userId);
    int insert(PriorityConfig config);
    int update(PriorityConfig config);
}