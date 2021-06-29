package com.chong.cmn.controller;

import com.chong.cmn.service.DictService;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/26 9:17 下午
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin // 防止产生跨域问题
public class DictController {
    @Autowired
    DictService dictService;


    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/queryChildData/{id}")
    public Result<List<Dict>> queryChildData(@PathVariable("id") Long id){
        List<Dict> dicts = dictService.queryChildData(id);
        return Result.ok(dicts);   //返回result会导致序列化问题
    }


    @ApiOperation("导出数据字典为Excel")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }


    @ApiOperation("导入Excel为数据字典")
    @PostMapping("/importData")
    public void importData(MultipartFile file){
        dictService.importData(file);
    }
}
