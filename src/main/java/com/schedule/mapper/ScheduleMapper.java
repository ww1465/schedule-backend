package com.schedule.mapper;

import com.schedule.entity.Schedule;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ScheduleMapper {
    // 新增 sortType 参数，和控制器对齐
    List<Schedule> selectByUserId(@Param("userId") Long userId, @Param("sortType") Integer sortType);

    int insert(Schedule schedule);

    int update(Schedule schedule);

    // 改为 根据ID + 用户ID 删除，防止删别人数据
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    // 根据ID + 用户ID 查询单条
    Schedule selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}