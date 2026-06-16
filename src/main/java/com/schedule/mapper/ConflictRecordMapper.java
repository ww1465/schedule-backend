package com.schedule.mapper;

import com.schedule.entity.ConflictRecord;
import org.apache.ibatis.annotations.Param;

public interface ConflictRecordMapper {
    int insert(ConflictRecord record);
}