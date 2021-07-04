package com.chong.gateway.controller.api;

import com.chong.gateway.service.DepartmentService;
import com.chong.gateway.service.HospitalService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.model.hosp.Department;
import com.chong.hospital.model.hosp.Hospital;
import com.chong.hospital.vo.hosp.DepartmentVo;
import com.chong.hospital.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/4 10:55 下午
 */
@Api(tags = "对接医院前端接口")
@RestController
@RequestMapping("/api/hosp/front")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;


    @ApiOperation("查询医院列表")
    @GetMapping("list/{page}/{limit}")
    public Result<Page<Hospital>> getHospList(@PathVariable("page") Integer page,
                                     @PathVariable("limit") Integer limit,
                                     HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }


    @ApiOperation("根据医院名称模糊查询医院列表")
    @GetMapping("/getByHosname/{hosname}")
    public Result<List<Hospital>> getByHosname(@PathVariable("hosname") String hosname){
        return Result.ok(hospitalService.getByHosname(hosname));
    }


    @ApiOperation("医院挂号详情")
    @GetMapping("{hoscode}")
    public Result<Map<String, Object>> getHospDetail(@PathVariable("hoscode") String hoscode){
        return Result.ok(hospitalService.showHospitalByHoscode(hoscode));
    }


    @ApiOperation("获取科室信息")
    @GetMapping("/department/{hoscode}")
    public Result<List<DepartmentVo>> getDepartmentList(@PathVariable("hoscode") String hoscode){
        return Result.ok(departmentService.getDeptList(hoscode));
    }
}
