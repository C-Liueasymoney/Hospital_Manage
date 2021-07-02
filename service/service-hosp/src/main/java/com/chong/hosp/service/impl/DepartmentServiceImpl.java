package com.chong.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.hosp.repository.DepartmentRepository;
import com.chong.hosp.service.DepartmentService;
import com.chong.hospital.model.hosp.Department;
import com.chong.hospital.vo.hosp.DepartmentQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/2 7:30 下午
 */
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        log.info("科室信息:{}", mapString);
        Department department = JSONObject.parseObject(mapString, Department.class);
        // 先查数据库中有没有对应的信息
        Department targetDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        // 如果数据库中已经存在科室信息,做更新，防止存入重复数据，这里把旧数据删除再插入（会有事务问题吗？）
        if (targetDepartment != null){
            departmentRepository.delete(targetDepartment);

            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else { // 不存在就创建
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        // 数据按照创建时间降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        // 0作为第一页
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)   //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true);   // 改变默认大小写忽略方式：忽略大小写

        // 创建实例
        Example<Department> example = Example.of(department, matcher);
        Page<Department> pages = departmentRepository.findAll(example, pageRequest);
        return pages;
    }


    @Override
    public void removeDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }
}
