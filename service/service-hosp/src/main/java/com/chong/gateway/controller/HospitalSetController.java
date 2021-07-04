package com.chong.gateway.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chong.gateway.service.HospitalSetService;
import com.chong.hospital.common.exception.NullHospSetIdException;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.result.ResultCodeEnum;
import com.chong.hospital.model.hosp.HospitalSet;
import com.chong.hospital.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.chong.hospital.common.utils.MD5;

import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/20 11:34 下午
 */
@Api(tags = "医院设置管理接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin  // 跨域访问
public class HospitalSetController {

//    注入service
    @Autowired
    private HospitalSetService hospitalSetService;

//    获取医院表中所有信息
    @ApiOperation(value = "获得所有的医院设置信息")
    @GetMapping("/queryAll")
    public Result<List<HospitalSet>> queryAllHospitalSet(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

//    删除操作
    @ApiOperation(value = "逻辑删除由id指定的医院设置信息")
    @DeleteMapping("/{id}")
    public Result removeHospitalSet(@ApiParam(value = "医院设置信息的id值") @PathVariable("id") Long id){
        boolean flag = hospitalSetService.removeById(id);
        return Result.flag(flag);
    }

//    条件查询带分页
    @ApiOperation(value = "具有条件查询功能的分页查询")
    @PostMapping("/queryPage/{current}/{size}")
    public Result queryPageHospitalSet(@PathVariable("current") long current,
                                       @PathVariable("size") long size,
                                       @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){ // 可以不带条件查询
        Page<HospitalSet> page = new Page<>(current, size);     // 先创建个page对象，传入当前页码和每页记录数
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();    // 拿到前端传来的医院名称和医院编号(封装在vo类中)
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();  // 创建一个查询条件类
        // 构建条件
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode", hoscode);
        }
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        return Result.ok(hospitalSetPage);
    }
//    添加医院设置
    @ApiOperation(value = "保存一条医院设置信息")
    @PostMapping("/save")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
//        设置一下状态1（可用）0（不可用）
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean flag = hospitalSetService.save(hospitalSet);
        return Result.flag(flag);
    }

//    根据id获取医院设置
    @ApiOperation(value = "根据id值来查询医院设置信息")
    @GetMapping("/query/{id}")
    public Result<HospitalSet> queryOneHospitalSet(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }


//    根据id集合获取医院设置
    @ApiOperation(value = "根据id值的集合查询医院设置信息")
    @GetMapping("/querys/{idList}")
    public Result<List<HospitalSet>> queryListHospitalSet(@PathVariable("idList") List<Long> idList){
        List<HospitalSet> hospitalSets = hospitalSetService.listByIds(idList);
        return Result.ok(hospitalSets);
    }


//    修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PostMapping("/update")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        return Result.flag(flag);
    }


//    批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("/batchDelete")
    public Result deleteHospitalSets(@RequestBody List<Long> idList){
        boolean flag = hospitalSetService.removeByIds(idList);
        return Result.flag(flag);
    }


//    设置医院锁定和解锁
    @ApiOperation(value = "设定医院锁定与解锁")
    @PutMapping("/lock/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id,
                                  @PathVariable("status") Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        try {
            hospitalSet.setStatus(status);
            updateHospitalSet(hospitalSet);
            return Result.ok(hospitalSet);
        } catch (NullPointerException e) {
            throw new NullHospSetIdException(ResultCodeEnum.NULL_HOSPITAL_SET);
        }
    }


//    发送签名密钥
    @ApiOperation(value = "发送签名密钥")
    @PutMapping("/send/{id}")
    public Result sendHospitalSet(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        try {
            String signKey = hospitalSet.getSignKey();
            String hoscode = hospitalSet.getHoscode();
            // TODO 发送短信
            return Result.ok();
        } catch (NullPointerException e) {
            throw new NullHospSetIdException(ResultCodeEnum.NULL_HOSPITAL_SET);
        }
    }
}

