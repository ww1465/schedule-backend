package com.schedule.service.impl;

import com.schedule.entity.PriorityConfig;
import com.schedule.entity.Schedule;
import com.schedule.mapper.PriorityConfigMapper;
import com.schedule.mapper.ScheduleMapper;
import com.schedule.service.ScheduleService;
import com.schedule.util.ScheduleUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private PriorityConfigMapper priorityConfigMapper;

    // ===================== 获取日程列表（含优先级计算与排序） =====================
    @Override
    public List<Schedule> getScheduleList(Integer sortType) {
        Long userId = ScheduleUtil.getUserId();

        // 1. 查出该用户的所有日程
        List<Schedule> list = scheduleMapper.selectByUserId(userId, sortType);

        // 2. 获取用户的优先级权重配置
        PriorityConfig config = priorityConfigMapper.selectByUserId(userId);

        // 3. 给每个日程计算优先级分数
        for (Schedule schedule : list) {
            double score = ScheduleUtil.calcPriorityScore(schedule, config);
            schedule.setPriorityScore(score);
        }

        // 4. 根据 sortType 选择排序规则
        if (sortType == null) {
            sortType = 2;   // 默认改为组合排序（分数降序 + 截止时间升序）
        }

        switch (sortType) {
            case 1:
                // EDF 截止时间排序
                list.sort(ScheduleUtil::compareEDF);
                break;
            case 2:
                // 组合排序：分数优先 + 截止时间为辅
                list.sort(ScheduleUtil::compareCombined);
                break;
            default:
                // 默认：按优先级分数排序
                list.sort(ScheduleUtil::compareByScore);
                break;
        }

        return list;
    }

    // ===================== 新增日程（已包含冲突检测） =====================
    @Override
    public String addSchedule(Schedule schedule) {
        Long userId = ScheduleUtil.getUserId();
        schedule.setUserId(userId);

        // 冲突检测
        List<Schedule> userSchedules = scheduleMapper.selectByUserId(userId, null);
        for (Schedule exist : userSchedules) {
            int conflict = ScheduleUtil.checkConflict(
                    exist.getStartTime(), exist.getEndTime(),
                    schedule.getStartTime(), schedule.getEndTime()
            );
            if (conflict > 0) {
                return "冲突类型：" + conflict + "，与日程《" + exist.getTitle() + "》时间冲突";
            }
        }

        scheduleMapper.insert(schedule);
        return "新增成功";
    }

    // ===================== 更新日程 =====================
    @Override
    public String updateSchedule(Schedule schedule) {
        Long userId = ScheduleUtil.getUserId();
        schedule.setUserId(userId);
        scheduleMapper.update(schedule);
        return "修改成功";
    }

    // ===================== 删除日程 =====================
    @Override
    public String deleteSchedule(Long id) {
        Long userId = ScheduleUtil.getUserId();
        int rows = scheduleMapper.deleteByIdAndUserId(id, userId);
        return rows > 0 ? "删除成功" : "删除失败，无权限或数据不存在";
    }

    // ===================== 获取默认优先级配置 =====================
    @Override
    public PriorityConfig getDefaultConfigByUserId() {
        Long userId = ScheduleUtil.getUserId();
        return priorityConfigMapper.selectByUserId(userId);
    }

    // ===================== 更新优先级配置 =====================
    @Override
    public String updateConfig(PriorityConfig config) {
        Long userId = ScheduleUtil.getUserId();
        config.setUserId(userId);
        priorityConfigMapper.update(config);
        return "更新成功";
    }
}