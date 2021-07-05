package com.chong.hospital.common.exception;

import com.chong.hospital.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 9:18 下午
 */
@ApiModel("自定义全局异常类（用户异常）")
public class UserException extends RuntimeException{

    @ApiModelProperty("异常状态码")
    private Integer code;

    /**
     * 使用状态码和错误信息创建异常对象
     * @param message
     * @param code
     */
    public UserException(String message, Integer code){
        super(message);
        this.code = code;
    }

    /**
     * 接受枚举类类型对象创建异常
     * @param resultCodeEnum
     */
    public UserException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "UserException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
