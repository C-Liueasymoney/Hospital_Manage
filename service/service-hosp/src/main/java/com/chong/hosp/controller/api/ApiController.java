package com.chong.hosp.controller.api;

import com.chong.hosp.service.HospitalService;
import com.chong.hosp.service.HospitalSetService;
import com.chong.hospital.common.helper.HttpRequestHelper;
import com.chong.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        // 获取医院系统中传递来的加密签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");

        // 根据传递的医院编号，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");


        // 调用service的方法
        hospitalService.save(paramMap);

        return Result.ok();
    }

}
