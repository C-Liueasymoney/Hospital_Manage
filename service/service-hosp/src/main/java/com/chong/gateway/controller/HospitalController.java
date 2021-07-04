package com.chong.gateway.controller;

import com.chong.gateway.service.HospitalService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.model.hosp.Hospital;
import com.chong.hospital.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 10:46 下午
 */
@Api(tags = "医院信息管理接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;


    @ApiOperation("获取分页列表")
    @GetMapping("/list/{page}/{limit}")
    public Result<Page<Hospital>> index(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable("page") Integer page,
                                        @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable("limit") Integer limit,
                                        @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = false)HospitalQueryVo hospitalQueryVo){

        return Result.ok(hospitalService.selectPage(page, limit, hospitalQueryVo));
    }


    @ApiOperation("更新医院上线状态")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@ApiParam(name = "id", value = "医院id", required = true)
                               @PathVariable("id") String id,
                               @ApiParam(name = "status", value = "医院上线状态", required = true)
                               @PathVariable("status") Integer status){

        hospitalService.updateStatus(id, status);
        return Result.ok();
    }


    @ApiOperation("获取单个医院详情")
    @GetMapping("/showHospDetail/{id}")
    public Result<Map<String, Object>> showHospital(@ApiParam(name = "id", value = "医院id", required = true)
                               @PathVariable("id") String id){

        return Result.ok(hospitalService.showHospital(id));
    }

}
