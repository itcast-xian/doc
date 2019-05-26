package cn.itcast.exception;

import cn.itcast.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    @ResponseBody
    public Result handleSerivceException(ServiceException e){
        return new Result(false,e.getErrMessage());
    }


    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Result handleSerivceException(Exception e){
        return new Result(false,"系统繁忙, 请稍后重试");
    }

}
