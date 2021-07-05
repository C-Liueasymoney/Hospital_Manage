package com.chong.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chong.hospital.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 8:37 下午
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
