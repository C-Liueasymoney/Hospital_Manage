package com.chong.hosp.service;

import com.chong.hospital.model.hosp.Schedule;
import com.chong.hospital.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 9:06 下午
 */
public interface ScheduleService {
    void saveSchedule(Map<String, Object> paramMap);

    Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    void removeSchedule(String hoscode, String hosScheduleId);
}
