package com.chong.msm.controller;

import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.utils.RandomUtil;
import com.chong.msm.service.MsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 11:48 下午
 */
@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable("phone") String phone){
        // 先从redis中获取验证码，如果拿到了就返回, key：手机号，value：验证码
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code))
            return Result.ok();

        // 从redis获取不到
        code = RandomUtil.getSixBitRandom();
        // 调用service来进行发送
        boolean isSend = msmService.send(phone, code);
        if (isSend){
            // 验证码放入redis，设置过期时间2分组
            redisTemplate.opsForValue().set(phone, code, 2, TimeUnit.SECONDS);
            return Result.ok();
        }else {
            return Result.fail().message("短信发送失败");
        }
    }
}
