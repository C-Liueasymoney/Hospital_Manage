package com.chong.user.controller;

import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.utils.IpUtil;
import com.chong.hospital.vo.user.LoginVo;
import com.chong.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayDeque;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 8:36 下午
 */
@Api(tags = "医院用户信息接口")
@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;


    @ApiOperation("登录接口")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo, HttpServletRequest request){
        String ipAddr = IpUtil.getIpAddr(request);
        loginVo.setIp(ipAddr);
        Map<String, Object> loginInfo = userInfoService.login(loginVo);
        return Result.ok(loginInfo);
    }
}
