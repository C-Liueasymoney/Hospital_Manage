package com.chong.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.hosp.repository.ScheduleRepository;
import com.chong.hosp.service.ScheduleService;
import com.chong.hospital.model.hosp.Schedule;
import com.chong.hospital.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 9:06 下午
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        Schedule targetSchedule = scheduleRepository.
                getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if (targetSchedule != null){   // 不为空更新
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);

            scheduleRepository.delete(targetSchedule);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }


    @Override
    public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        // 数据按照创建时间降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 0作为第一页
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        // 创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)   //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true);   // 改变默认大小写忽略方式：忽略大小写

        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> pages = scheduleRepository.findAll(example, pageRequest);
        return pages;
    }


    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}
