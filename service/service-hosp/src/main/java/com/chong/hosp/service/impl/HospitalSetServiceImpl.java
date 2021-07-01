package com.chong.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chong.hosp.mapper.HospitalSetMapper;
import com.chong.hosp.service.HospitalSetService;
import com.chong.hospital.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/20 11:26 下午
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }
}
