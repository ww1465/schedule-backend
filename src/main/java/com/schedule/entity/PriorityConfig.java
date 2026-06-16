package com.schedule.entity;
import lombok.Data;

@Data
public class PriorityConfig {
    private Long id;
    private Long userId;
    private Double urgencyWeight;
    private Double importanceWeight;
    private Double deadlineWeight;
    private Double durationWeight;
    private Double historyWeight;
    private Integer isAutoIter;
}