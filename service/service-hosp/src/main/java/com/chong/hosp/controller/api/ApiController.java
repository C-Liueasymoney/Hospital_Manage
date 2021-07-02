package com.chong.hosp.controller.api;

import com.chong.hosp.service.DepartmentService;
import com.chong.hosp.service.HospitalService;
import com.chong.hosp.service.HospitalSetService;
import com.chong.hosp.service.ScheduleService;
import com.chong.hospital.common.exception.WrongHospSetException;
import com.chong.hospital.common.helper.HttpRequestHelper;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.result.ResultCodeEnum;

import com.chong.hospital.model.hosp.Department;
import com.chong.hospital.model.hosp.Schedule;
import com.chong.hospital.vo.hosp.DepartmentQueryVo;
import com.chong.hospital.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/30 11:04 下午
 */

@Api(tags = "医院信息对接接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;


    // 上传医院接口
    @ApiOperation("上传医院信息接口")
    @PostMapping("/saveHospital")
    public Result savaHosp(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        // 获取医院传递过来的医院信息
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 根据传递的医院编号，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");

        // 防止查询不到医院编号
        if (StringUtils.isEmpty(hoscode))
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);


        //传输过程中“+”转换为了“ ”，因此我们要转换回来,
        String logoDataString = (String) paramMap.get("logoData");
        if (!StringUtils.isEmpty(logoDataString)){
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }

        // 判断签名是否一致，这里的方法已经封装好了根据传入的参数map和本地数据库签名判断签名是否相同
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        // 调用service的方法
        hospitalService.save(paramMap);

        return Result.ok();
    }



    // 查询医院信息的接口
    @ApiOperation("查询医院信息的接口")
    @PostMapping("/hospital/show")
    public Result getHospital(HttpServletRequest request){
        // 获取医院传递过来的医院信息
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 获取传递的医院编号,并做参数校验
        String hoscode = (String)paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }

        //签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        return Result.ok(hospitalService.getByHoscode((String)paramMap.get("hoscode")));
    }


    @ApiOperation("保存科室信息")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.saveDepartment(paramMap);

        return Result.ok();
    }


    @ApiOperation("查询科室信息")
    @PostMapping("/department/list")
    public Result<Page<Department>> queryDepartment(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }
        // 判断pageNum是否为空,赋默认值1
        Integer pageNum = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        // 赋值每页显示记录数
        Integer limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));

        // 创建VO实体
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.selectPage(pageNum, limit, departmentQueryVo);

        return Result.ok(pageModel);
    }


    @ApiOperation("删除科室信息")
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        String depcode = (String) paramMap.get("depcode");

        departmentService.removeDepartment(hoscode, depcode);
        return Result.ok();
    }


    @ApiOperation("上传排班接口")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.saveSchedule(paramMap);
        return Result.ok();
    }


    @ApiOperation("查询排班分页列表")
    @PostMapping("/schedule/list")
    public Result<Page<Schedule>> querySchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        Integer pageNum = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        Integer limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        Page<Schedule> pageModel = scheduleService.selectPage(pageNum, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation("删除排班")
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 参数校验
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new WrongHospSetException(ResultCodeEnum.PARAM_ERROR);
        }
        // 签名校验
        if (!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))){
            throw new WrongHospSetException(ResultCodeEnum.SIGN_ERROR);
        }

        String hosScheduleId = (String) paramMap.get("hosScheduleId");

        scheduleService.removeSchedule(hoscode, hosScheduleId);
        return Result.ok();
    }
}
