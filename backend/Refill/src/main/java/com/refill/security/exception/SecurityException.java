package com.refill.security.exception;

import com.refill.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityException extends RuntimeException{

    private ErrorCode errorCode;

}
