package com.chong.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chong.hospital.model.user.UserInfo;
import com.chong.hospital.vo.user.LoginVo;

import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 8:37 下午
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    // 根据微信openid获取用户信息
    UserInfo getByOpenid(String openid);

}
