package com.chong.hospital.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/21 8:22 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("全局统一的返回结果")
public class Result<T> {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("返回消息")
    private String message;
    @ApiModelProperty("返回数据")
    private T data;

    protected static <T> Result<T> build(T data){
        Result<T> result = new Result<>();
        if (data != null){
            result.setData(data);
        }
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum){
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> build(Integer code, String message){
        Result<T> result = build(null);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> ok(){
        return Result.ok(null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data){
//        if (data == null)
//            return fail(data);
//        else                   // 这部分逻辑有点问题，后面再看看

        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> fail(){
        return Result.fail(null);
    }


    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data){
        return build(data, ResultCodeEnum.FAIL);
    }

    /**
     * 判断输入的结果是否为正确
     * @param <T>:操作给出的结果true or false
     * @return
     */
    public static <T> Result<T> flag(boolean flag){
        if (flag)
            return ok();
        else
            return fail();
    }


    public static <T> Result<T> null_hospital(){
        return null_hospital(null);
    }

    /**
     * 在医院设置中没有所查询的医院id时返回
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> null_hospital(T data){
        return build(data, ResultCodeEnum.NULL_HOSPITAL_SET);
    }



    public Result<T> message(String message){
        this.setMessage(message);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }

    public boolean isOk(){
        if (this.getCode().intValue() == ResultCodeEnum.SUCCESS.getCode().intValue())
            return true;
        else
            return false;
    }

}
