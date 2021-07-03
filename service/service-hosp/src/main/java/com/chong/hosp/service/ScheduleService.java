package com.chong.hosp.service;

import com.chong.hospital.model.hosp.Schedule;
import com.chong.hospital.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
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

    // 根据医院编号和科室编号获取排班数据
    Map<String, Object> getRuleSchedule(Long page, Long limit, String hoscode, String depcode);

    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);
}
