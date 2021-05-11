package com.github.nekolr.slime.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("出现未知异常", e);
        ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR.value(), ExceptionUtils.getStackTrace(e));
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST.value(), this.getErrorMessage(errorList));
        return this.buildResponseEntity(errorResponse);
    }


    /**
     * 获取错误消息
     *
     * @param errorList
     * @return
     */
    private String getErrorMessage(List<ObjectError> errorList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error : errorList) {
            String message = error.getDefaultMessage();
            stringBuilder.append(message + ", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }


    /**
     * 创建响应实体
     *
     * @param errorResponse
     * @return
     */
    private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

}
