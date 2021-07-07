package com.chong.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chong.hospital.common.exception.UserException;
import com.chong.hospital.common.result.ResultCodeEnum;
import com.chong.hospital.common.util.JwtHelper;
import com.chong.hospital.model.user.UserInfo;
import com.chong.hospital.model.user.UserLoginRecord;
import com.chong.hospital.vo.user.LoginVo;
import com.chong.user.mapper.UserInfoMapper;
import com.chong.user.mapper.UserLoginRecordMapper;
import com.chong.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 8:50 下午
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    UserLoginRecordMapper userLoginRecordMapper;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String phoneNumber = loginVo.getPhone();
        String code = loginVo.getCode();
//        数据校验
        if (StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(code)){
            throw new UserException(ResultCodeEnum.PARAM_ERROR);
        }

        //TODO 校验验证码
        String mobileCode = redisTemplate.opsForValue().get(phoneNumber);
        if (!code.equals(mobileCode)){
            throw new UserException(ResultCodeEnum.CODE_ERROR);
        }

        // 绑定手机号
        UserInfo userInfo = null;
        if (!StringUtils.isEmpty(loginVo.getOpenid())){
            userInfo = this.getByOpenid(loginVo.getOpenid());
        }
        if (userInfo != null) {
            userInfo.setPhone(loginVo.getPhone());
            this.updateById(userInfo);
        }



        // userinfo==null，说明用手机登录
        if (userInfo == null) {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phoneNumber);
            // 检查手机号是否已经注册
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {   // 如果为空，新创建一个用户信息
                userInfo = new UserInfo();
                userInfo.setPhone(phoneNumber);
                userInfo.setName("");
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }

        // 校验用户状态是否被禁用
        if (userInfo.getStatus() == 0){
            throw new UserException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //TODO 记录登录
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(loginVo.getIp());
        userLoginRecordMapper.insert(userLoginRecord);

        //返回页面显示名称
        Map<String, Object> result = new HashMap<>();
        String userName = userInfo.getName();
        if (StringUtils.isEmpty(userName)){
            userName = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(userName)){
            userName = userInfo.getPhone();
        }

        result.put("name", userName);

        // TODO token生成
        result.put("token", JwtHelper.createToken(userInfo.getId(), userName));
        return result;
    }


    @Override
    public UserInfo getByOpenid(String openid) {
        return baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid", openid));
    }
}
