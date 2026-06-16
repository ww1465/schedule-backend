package com.schedule.entity;
import lombok.Data;

@Data
public class Schedule {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    // 把这里改成 String
    private String startTime;
    private String endTime;
    private String location;
    private Integer type;
    private String tag;
    private Integer importance;
    private Integer urgency;
    private Integer status;
    private String repeatRule;
    private Double priorityScore;
}