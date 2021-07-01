package com.chong.hospital.common.exception;

import com.chong.hospital.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/1 9:32 下午
 */
@Data
@ApiModel("自定义异常类（医院设置发生错误时抛出）")
public class WrongHospSetException extends RuntimeException{

    @ApiModelProperty("异常状态码")
    private Integer code;

    /**
     * 使用状态码和错误信息创建异常对象
     * @param message
     * @param code
     */
    public WrongHospSetException(String message, Integer code){
        super(message);
        this.code = code;
    }

    /**
     * 接受枚举类类型对象创建异常
     * @param resultCodeEnum
     */
    public WrongHospSetException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "WrongHospSetSignException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
