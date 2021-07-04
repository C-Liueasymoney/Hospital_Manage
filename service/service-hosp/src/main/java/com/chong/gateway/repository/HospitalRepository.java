package com.chong.gateway.repository;

import com.chong.hospital.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/30 11:02 下午
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    // 查询Hoscode判断是否存在相同数据
    Hospital getHospitalByHoscode(String hoscode);    // mongoRepository会根据方法名自动实现操作

    // 根据hosname模糊查询
    List<Hospital> getHospitalsByHosnameLike(String hosname);
}
