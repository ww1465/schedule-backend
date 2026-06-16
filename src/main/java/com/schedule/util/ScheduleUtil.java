package com.schedule.util;

import com.schedule.entity.PriorityConfig;
import com.schedule.entity.Schedule;
import com.schedule.entity.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final long GAP_MIN = 10;

    // ===================== 获取登录用户相关方法 =====================
    /**
     * 获取当前登录用户对象
     */
    public static User getLoginUser() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        // 从 request 属性取用户，不再用 session
        return (User) request.getAttribute("loginUser");
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getId();
    }
    // =====================================================================

    // 五因子加权打分
    public static Double calcPriorityScore(Schedule schedule, PriorityConfig config) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.parse(schedule.getStartTime(), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(schedule.getEndTime(), FORMATTER);

        // ====== 手动优先 + 自动计算紧急度 ======
        int finalUrgency = (schedule.getUrgency() != null)
                ? schedule.getUrgency()
                : autoCalculateUrgency(schedule);

        double urgencyScore = (finalUrgency / 5.0) * config.getUrgencyWeight() * 100;
        // ====================================

        double importanceScore = (schedule.getImportance() / 5.0) * config.getImportanceWeight() * 100;

        Duration deadlineDur = Duration.between(now, endTime);
        double deadlineScore = getTimeNormalScore(deadlineDur.toHours()) * config.getDeadlineWeight() * 100;

        Duration taskDur = Duration.between(startTime, endTime);
        double durationScore = getTimeNormalScore(taskDur.toHours()) * config.getDurationWeight() * 100;

        double historyScore = 0.5 * config.getHistoryWeight() * 100;

        return urgencyScore + importanceScore + deadlineScore + durationScore + historyScore;
    }

    // ====== 自动计算紧急度 1~5 ======
    private static int autoCalculateUrgency(Schedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.parse(schedule.getEndTime(), FORMATTER);
        long hours = Duration.between(now, endTime).toHours();

        if (hours <= 1) {
            return 5;
        } else if (hours <= 3) {
            return 4;
        } else if (hours <= 6) {
            return 3;
        } else if (hours <= 12) {
            return 2;
        } else {
            return 1;
        }
    }
    // ==============================

    // 时间归一化 0~1
    private static double getTimeNormalScore(long hour) {
        if (hour <= 0) return 1.0;
        if (hour > 24) return 0.0;
        return 1 - (hour / 24.0);
    }

    // 冲突检测
    public static int checkConflict(String s1Str, String e1Str, String s2Str, String e2Str) {
        LocalDateTime s1 = LocalDateTime.parse(s1Str, FORMATTER);
        LocalDateTime e1 = LocalDateTime.parse(e1Str, FORMATTER);
        LocalDateTime s2 = LocalDateTime.parse(s2Str, FORMATTER);
        LocalDateTime e2 = LocalDateTime.parse(e2Str, FORMATTER);

        boolean crossDay = !LocalDate.from(s1).equals(LocalDate.from(e1))
                || !LocalDate.from(s2).equals(LocalDate.from(e2));

        // 1 完全重叠
        if (s2.isAfter(s1) && e2.isBefore(e1)) {
            return crossDay ? 3 : 1;
        }
        // 2 部分重叠
        if ((s1.isBefore(s2) && s2.isBefore(e1)) || (s2.isBefore(s1) && s1.isBefore(e2))) {
            return crossDay ? 3 : 2;
        }
        // 4 短时衔接冲突
        Duration gap1 = Duration.between(e1, s2);
        Duration gap2 = Duration.between(e2, s1);
        if (Math.abs(gap1.toMinutes()) < GAP_MIN || Math.abs(gap2.toMinutes()) < GAP_MIN) {
            return 4;
        }
        // 0 无冲突
        return 0;
    }

    // 按优先级分数降序排序
    public static int compareByScore(Schedule s1, Schedule s2) {
        return Double.compare(s2.getPriorityScore(), s1.getPriorityScore());
    }

    // EDF 截止时间优先排序
    public static int compareEDF(Schedule s1, Schedule s2) {
        LocalDateTime e1 = LocalDateTime.parse(s1.getEndTime(), FORMATTER);
        LocalDateTime e2 = LocalDateTime.parse(s2.getEndTime(), FORMATTER);
        return e1.compareTo(e2);
    }

    // 组合排序
    public static int compareCombined(Schedule s1, Schedule s2) {
        int scoreComp = compareByScore(s1, s2);
        if (scoreComp != 0) {
            return scoreComp;
        }
        return compareEDF(s1, s2);
    }
}