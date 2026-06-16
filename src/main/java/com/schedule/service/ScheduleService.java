package com.schedule.service;

import com.schedule.entity.PriorityConfig;
import com.schedule.entity.Schedule;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getScheduleList(Integer sortType);
    String addSchedule(Schedule schedule);
    String updateSchedule(Schedule schedule);
    String deleteSchedule(Long id);
    PriorityConfig getDefaultConfigByUserId();
    String updateConfig(PriorityConfig config);
}