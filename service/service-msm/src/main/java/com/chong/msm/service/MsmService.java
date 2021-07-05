package com.chong.msm.service;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 11:38 下午
 */
public interface MsmService {

    // 发送手机验证码
    boolean send(String phone, String code);
}
