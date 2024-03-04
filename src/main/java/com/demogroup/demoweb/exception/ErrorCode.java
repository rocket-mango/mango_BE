package com.demogroup.demoweb.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_DUPLICATED(HttpStatus.CONFLICT),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND),
    DISEASE_NOT_FOUND(HttpStatus.NOT_FOUND),
    JSON_PARSE_FAILED(HttpStatus.CONFLICT);
    private HttpStatus httpStatus;

}
