package com.chong.hosp.service;


import com.chong.hospital.model.hosp.Department;
import com.chong.hospital.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 7:30 下午
 */
public interface DepartmentService {

    void saveDepartment(Map<String, Object> paramMap);

    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    void removeDepartment(String hoscode, String depcode);
}
