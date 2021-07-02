package com.chong.hosp.service;

import com.chong.hospital.model.hosp.Hospital;
import com.chong.hospital.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/30 11:03 下午
 */
public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
