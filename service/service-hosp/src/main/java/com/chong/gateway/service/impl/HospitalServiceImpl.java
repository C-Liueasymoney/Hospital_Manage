package com.chong.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chong.cmn.client.DictFeignClient;
import com.chong.gateway.repository.HospitalRepository;
import com.chong.gateway.service.HospitalService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.enums.DictEnum;
import com.chong.hospital.model.hosp.Hospital;
import com.chong.hospital.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    DictFeignClient dictFeignClient;

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

        // 通过服务调用获取信息, pages.getContent()得到pages里面的数据集合List<Hospital>
        List<Hospital> content = pages.getContent();   // 利用里面的param（BaseMongoEntity中属性）封装其医院登记（原本的字段中不存在）

        // lambda表达式，就是把content中保存的所有hospital信息依次传入packHospital函数中处理
        content.stream().forEach(item -> {
            this.packHospital(item);
        });

        return pages;
    }

    /**
     * 封装数据
     * @param hospital
     * @return
     */
    private Hospital packHospital(Hospital hospital){
        //TODO 这个部分的调用过程以及数据表中的字段对应需要好好总结
        Result<String> hostypeResult = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        String hostypeString = hostypeResult.getData();

        String provinceString = dictFeignClient.getName(hospital.getProvinceCode()).getData();

        String cityString = dictFeignClient.getName(hospital.getCityCode()).getData();

        String districtString = dictFeignClient.getName(hospital.getDistrictCode()).getData();

        // 这里往其他参数param中添加保存
        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());

        return hospital;
    }


    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }


    @Override
    public Map<String, Object> showHospital(String id) {
        Map<String, Object> result = new HashMap<>();

        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get());
        // 将医院等级和地址打包进hospital对象中
         result.put("hospital", hospital);

        // 单独处理预约规则
        result.put("bookingRule", hospital.getBookingRule());

        // hopital中的就可以移除了
        hospital.setBookingRule(null);
        return result;
    }


    @Override
    public Map<String, Object> showHospitalByHoscode(String hoscode) {
        return this.showHospital(this.getByHoscode(hoscode).getId());
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null){
            return hospital.getHosname();
        }else {
            return "";
        }
    }

    @Override
    public List<Hospital> getByHosname(String hosname) {
        return hospitalRepository.getHospitalsByHosnameLike(hosname);
    }
}
