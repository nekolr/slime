package com.github.nekolr.slime.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 无效或错误的请求
 */
@Getter
public class BadRequestException extends RuntimeException {

    private Integer status = HttpStatus.BAD_REQUEST.value();

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus.value();
    }
}
