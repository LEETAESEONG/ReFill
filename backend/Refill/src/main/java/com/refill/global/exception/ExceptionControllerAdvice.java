package com.refill.global.exception;

import com.refill.account.exception.AccountException;
import com.refill.member.exception.MemberException;
import com.refill.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<String> memberExceptionHandler(MemberException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode()
                                      .getHttpStatus())
            .body(e.getErrorCode().getCode() + " : " + e.getErrorCode().name() + " " + e.getMessage());
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<String> accountExceptionHandler(AccountException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode()
                                      .getHttpStatus())
                             .body(e.getErrorCode().getCode() + " : " + e.getErrorCode().name() + " " + e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> securityExceptionHandler(SecurityException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode()
                                      .getHttpStatus())
                             .body(e.getErrorCode().getCode() + " : " + e.getErrorCode().name() + " " + e.getMessage());
    }
}
