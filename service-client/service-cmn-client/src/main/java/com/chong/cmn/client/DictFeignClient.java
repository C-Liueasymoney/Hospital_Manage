package com.chong.cmn.client;

import com.chong.hospital.common.result.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/3 12:00 上午
 */
@FeignClient("service-cmn")
public interface DictFeignClient {

    @ApiOperation("根据字典码和值获取数据字典名称")
    @GetMapping("/admin/cmn/dict/getName/{parentDictCode}/{value}")
    Result<String> getName(@ApiParam(name = "parentDictCode", value = "上级编码", required = true) @PathVariable("parentDictCode") String parentDictCode,
                                  @ApiParam(name = "value", value = "数据值", required = true) @PathVariable("value") String value);



    @ApiOperation("根据值获取数据字典名称")
    @GetMapping("/admin/cmn/dict/getName/{value}")
    Result<String> getName(@ApiParam(name = "value", value = "数据值", required = true) @PathVariable("value") String value);
}
