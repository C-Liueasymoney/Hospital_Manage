package com.chong.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.hosp.repository.HospitalRepository;
import com.chong.hosp.service.HospitalService;
import com.chong.hospital.model.hosp.Hospital;
import com.chong.hospital.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
            // 如果存在，更新,防止存入重复数据，这里把旧数据删除再插入（会有事务问题吗？）
            hospitalRepository.delete(hospitalExist);

            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 数据按照创建时间降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 0作为第一页
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)   //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true);   // 改变默认大小写忽略方式：忽略大小写

        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageRequest);
        return pages;
    }
}
