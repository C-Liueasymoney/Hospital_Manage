package com.chong.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chong.hospital.model.user.UserLoginRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/7 11:51 上午
 */
@Mapper
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {
}
