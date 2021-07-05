package com.chong.hospital.common.exception;

import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/6/23 12:00 上午
 */
//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(NullHospSetIdException.class)
    public Result nullHospSetIdExceptionHandler(NullHospSetIdException e){
//        e.printStackTrace();
        return Result.null_hospital();
    }

    @ExceptionHandler(UserException.class)
    public Result userException(UserException e){
        return Result.fail().message(e.getMessage());
    }
}
