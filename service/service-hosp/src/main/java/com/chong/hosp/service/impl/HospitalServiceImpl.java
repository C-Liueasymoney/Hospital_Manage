package com.chong.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.hosp.repository.HospitalRepository;
import com.chong.hosp.service.HospitalService;
import com.chong.hospital.model.hosp.Hospital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/30 11:03 下午
 */
@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    HospitalRepository hospitalRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        log.info("医院信息：" + JSONObject.toJSONString(paramMap));
        // 把参数map集合转换为Hospital对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        // 先判断是否存在相同数据
        String hoscode = hospital.getHoscode();     // 根据hoscode（唯一键）查看是否有相同数据
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        // 如果不存在，添加
        if (hospitalExist == null){
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {
            // 如果存在，更新
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }
}
