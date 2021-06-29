package com.chong.hospital.common.exception;

import com.chong.hospital.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/23 12:20 上午
 */
@Data
@ApiModel("自定义全局异常类（医院设置不存在时抛出）")
public class NullHospSetIdException extends RuntimeException{

    @ApiModelProperty("异常状态码")
    private Integer code;

    /**
     * 使用状态码和错误信息创建异常对象
     * @param message
     * @param code
     */
    public NullHospSetIdException(String message, Integer code){
        super(message);
        this.code = code;
    }

    /**
     * 接受枚举类类型对象创建异常
     * @param resultCodeEnum
     */
    public NullHospSetIdException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "NullHospSetIdException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
