package com.chong.hosp.controller;

import com.chong.hosp.service.DepartmentService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/3 4:16 下午
 */
@Api(tags = "科室管理接口")
@RestController
@RequestMapping("/admin/hosp/department")
@CrossOrigin
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;


    @ApiOperation("根据医院编号查询所有科室列表")
    @GetMapping("/getDeptList/{hoscode}")
    public Result<List<DepartmentVo>> getDeptList(@ApiParam(name = "hoscode", value = "医院编号", required = true)
                                            @PathVariable("hoscode") String hoscode){
        return Result.ok(departmentService.getDeptList(hoscode));
    }


}
