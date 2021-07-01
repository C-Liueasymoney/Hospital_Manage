package com.chong.hosp.controller.api;

import com.chong.hosp.service.HospitalService;
import com.chong.hosp.service.HospitalSetService;
import com.chong.hospital.common.exception.WrongHospSetException;
import com.chong.hospital.common.helper.HttpRequestHelper;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.result.ResultCodeEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
}
