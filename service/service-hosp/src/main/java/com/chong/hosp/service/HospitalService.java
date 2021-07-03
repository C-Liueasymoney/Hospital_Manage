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

    void updateStatus(String id, Integer status);

    Map<String, Object> showHospital(String id);

    // 根据医院编号获取医院名称
    String getHospName(String hoscode);
}
