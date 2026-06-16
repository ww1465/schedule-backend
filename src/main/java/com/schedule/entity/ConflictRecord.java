package com.schedule.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConflictRecord {
    private Long id;
    private Long scheduleId;
    private Long conflictScheduleId;
    private Integer conflictType;
    private LocalDateTime createTime;
}