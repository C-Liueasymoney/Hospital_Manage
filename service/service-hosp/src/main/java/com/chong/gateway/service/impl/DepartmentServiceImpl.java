package com.chong.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.gateway.repository.DepartmentRepository;
import com.chong.gateway.service.DepartmentService;
import com.chong.hospital.model.hosp.Department;
import com.chong.hospital.vo.hosp.DepartmentQueryVo;
import com.chong.hospital.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Override
    // 根据医院编号来查询医院所有的科室列表
    public List<DepartmentVo> getDeptList(String hoscode) {
        List<DepartmentVo> result = new ArrayList<>();

        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        // 由于hoscode不是id，所以没有原生方法，需要构建一个example，传入一个department对象，其中除了hoscode其他属性都为null
        // 就按照hoscode来获取对应的数据
        Example<Department> example = Example.of(departmentQuery);

        List<Department> departments = departmentRepository.findAll(example);

        // stream流操作，将departments列表中的每个实例都根据Department类中的Bigcode字段进行分组，获得一个映射map
        // 其中map的key是bigcode，value是所属同一bigcode的department列表
        Map<String, List<Department>> departmentMap = departments.stream().collect(Collectors.groupingBy(Department::getBigcode));

        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()){
            // 得到大科室编号,这个编号是一串字符，每个大科室对应同一个
            String bigCode = entry.getKey();
            // 得到大科室对应的科室集合
            List<Department> departmentsList = entry.getValue();
            // 创建一个DepartmentVo来作为大科室
            DepartmentVo bigDepartment = new DepartmentVo();
            // 为大科室设置depname和depcode
            bigDepartment.setDepcode(bigCode);
            bigDepartment.setDepname(departmentsList.get(0).getBigname());   // 大科室的名字自然就是其附属科室的bigName

            // 创建包含小科室的list,来作为大科室的下级节点children
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : departmentsList){
                DepartmentVo childDepartment = new DepartmentVo();
                childDepartment.setDepname(department.getDepname());
                childDepartment.setDepcode(department.getDepcode());
                children.add(childDepartment);
            }

            // 把小科室列表添加到大科室的children字段中
            bigDepartment.setChildren(children);
            // 放入最终返回的列表中
            result.add(bigDepartment);
        }
        return result;
    }

    @Override
    public String getDeptName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null)
            return department.getDepname();
        else
            return null;
    }
}
