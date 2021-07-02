package com.chong.hosp.repository;

import com.chong.hospital.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 7:27 下午
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}

