package com.chong.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.gateway.repository.ScheduleRepository;
import com.chong.gateway.service.DepartmentService;
import com.chong.gateway.service.HospitalService;
import com.chong.gateway.service.ScheduleService;
import com.chong.hospital.model.hosp.Schedule;
import com.chong.hospital.vo.hosp.BookingScheduleRuleVo;
import com.chong.hospital.vo.hosp.ScheduleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

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


    @Override
    //根据医院编号 和 科室编号 ，查询排班规则数据
    public Map<String, Object> getRuleSchedule(Long page, Long limit, String hoscode, String depcode) {
        //1 根据医院编号 和 科室编号 查询
        Criteria criteria = new Criteria().where("hoscode").is(hoscode).and("depcode").is(depcode);
        // 2、根据工作schedule表中的工作日期workdate进行分组

        Aggregation ag = Aggregation.newAggregation(Aggregation.match(criteria), // 先进行匹配过滤出医院编号和科室编号相等的字段
                Aggregation.group("workDate").    // 然后在过滤结束的数据中按照workDate进行分组
                        first("workDate").as("workDate").  // 获取分组结束之后每个组workDate字段的首个值,计作workDate
                        count().as("docCount").    // 统计号源数量，也就是每组workDate（工作日）有多少医生工作，计作docCount
                        sum("reservedNumber").as("reservedNumber").     // 统计每日所有医生的放号数量总和，计作reservedNumber
                        sum("availableNumber").as("availableNumber"),   // 统计每日所有医生的可用数量和，计作availableNumber
                Aggregation.sort(Sort.Direction.DESC, "workDate"),     // 结果排序
                Aggregation.skip((page - 1) * limit), Aggregation.limit(limit));  // 最后实现分页

        // 调用方法执行上面的聚合条件，返回的结果封装为BookingScheduleRuleVo放入list中
        AggregationResults<BookingScheduleRuleVo> aggResult = mongoTemplate.aggregate(ag, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookScheduleRuleVoList = aggResult.getMappedResults();

        // 得到分组查询的总记录数
        Aggregation agTotal = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate"));
        AggregationResults<BookingScheduleRuleVo> agTotalRes = mongoTemplate.aggregate(agTotal, Schedule.class, BookingScheduleRuleVo.class);
        int total = agTotalRes.getMappedResults().size();


        // 将日期所对应的星期几获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookScheduleRuleVoList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终数据返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookScheduleRuleVoList);
        result.put("total", total);
        // 获取医院名称并设置
        String hospName = hospitalService.getHospName(hoscode);
        // 用其他的基础数据放入baseMap再放入result中
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospName);

        result.put("baseMap", baseMap);

        return result;
    }


    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList = scheduleRepository.getSchedulesByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        // 遍历集合向其中的其他数据放入相应数据
        scheduleList.forEach(this::packSchedule);
        return scheduleList;
    }

    /**
     * 根据日期来获取周几的信息
     */
    private String getDayOfWeek(DateTime dateTime){
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()){
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;

        }
        return dayOfWeek;
    }


    // 封装排班信息
    private void packSchedule(Schedule schedule){
        // 设置医院名称放入额外参数
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        // 设置科室名称
        schedule.getParam().put("depname", departmentService.getDeptName(schedule.getHoscode(), schedule.getDepcode()));
        // 设置日期对应的星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }
}

