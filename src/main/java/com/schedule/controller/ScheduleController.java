package com.schedule.controller;

import com.schedule.entity.PriorityConfig;
import com.schedule.entity.Schedule;
import com.schedule.entity.User;
import com.schedule.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    // ========== 日程列表 ==========
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) Integer sortType) {
        Map<String, Object> result = new HashMap<>();
        List<Schedule> list = scheduleService.getScheduleList(sortType);
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("data", list);
        return result;
    }

    // ========== 新增日程 ==========
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Schedule schedule, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 从拦截器写入的 loginUser 中取出当前用户
        User loginUser = (User) request.getAttribute("loginUser");
        if (loginUser != null) {
            schedule.setUserId(loginUser.getId());
        } else {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        String msg = scheduleService.addSchedule(schedule);
        if (msg.contains("冲突")) {
            result.put("code", 409);
            result.put("msg", msg);
        } else {
            result.put("code", 200);
            result.put("msg", "新增成功");
        }
        return result;
    }

    // ========== 更新日程 ==========
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Schedule schedule, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        User loginUser = (User) request.getAttribute("loginUser");
        if (loginUser != null) {
            schedule.setUserId(loginUser.getId());
        } else {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        String msg = scheduleService.updateSchedule(schedule);
        if (msg.contains("失败")) {
            result.put("code", 500);
            result.put("msg", msg);
        } else {
            result.put("code", 200);
            result.put("msg", "修改成功");
        }
        return result;
    }

    // ========== 删除日程 ==========
    @DeleteMapping("/del/{id}")
    public Map<String, Object> del(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 删除时也可以显式校验，但 Service 内部已经验证用户权限，此处非必须
        User loginUser = (User) request.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        String msg = scheduleService.deleteSchedule(id);
        if (msg.contains("失败")) {
            result.put("code", 500);
            result.put("msg", msg);
        } else {
            result.put("code", 200);
            result.put("msg", "删除成功");
        }
        return result;
    }

    // ========== 获取优先级配置 ==========
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> result = new HashMap<>();
        PriorityConfig config = scheduleService.getDefaultConfigByUserId();
        result.put("code", 200);
        result.put("msg", "查询成功");
        result.put("data", config);
        return result;
    }

    // ========== 更新优先级配置 ==========
    @PutMapping("/config/update")
    public Map<String, Object> updateConfig(@RequestBody PriorityConfig config, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        User loginUser = (User) request.getAttribute("loginUser");
        if (loginUser != null) {
            config.setUserId(loginUser.getId());
        } else {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }

        String msg = scheduleService.updateConfig(config);
        if (msg.contains("失败")) {
            result.put("code", 500);
            result.put("msg", msg);
        } else {
            result.put("code", 200);
            result.put("msg", "更新成功");
        }
        return result;
    }
}