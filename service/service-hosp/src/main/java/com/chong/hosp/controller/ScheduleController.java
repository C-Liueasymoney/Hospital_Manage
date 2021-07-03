package com.chong.hosp.controller;

import com.chong.hosp.service.ScheduleService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/3 5:15 下午
 */
@Api(tags = "查询排班规则数据")
@RestController
@RequestMapping("/admin/hosp/schedule")
@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 根据医院编号和科室编号，查询排班规则数据
    @ApiOperation("查询排班规则数据")
    @GetMapping("/getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result<Map<String, Object>> getScheduleRule(@ApiParam(name = "page", value = "当前页", required = true)
                                  @PathVariable("page") Long page,
                                  @ApiParam(name = "limit", value = "每页记录数", required = true)
                                  @PathVariable("limit") Long limit,
                                  @ApiParam(name = "hoscode", value = "医院编号", required = true)
                                  @PathVariable("hoscode") String hoscode,
                                  @ApiParam(name = "depcode", value = "科室编号", required = true)
                                  @PathVariable("depcode") String depcode){

        Map<String, Object> ruleSchedule = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(ruleSchedule);
    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation("显示排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result<List<Schedule>> getScheduleDetail(@PathVariable("hoscode") String hoscode,
                                                    @PathVariable("depcode") String depcode,
                                                    @PathVariable("workDate") String workDate){

        return Result.ok(scheduleService.getScheduleDetail(hoscode, depcode, workDate));
    }

}
