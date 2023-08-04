package com.refill.global.exception;

import com.refill.account.exception.AccountException;
import com.refill.aidiagnosis.exception.AiDiagnosisException;
import com.refill.global.dto.response.ApiErrorResponse;
import com.refill.member.exception.MemberException;
import com.refill.review.exception.ReviewException;
import com.refill.security.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiErrorResponse> memberExceptionHandler(MemberException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(new ApiErrorResponse(e.getErrorCode().getCode(), e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ApiErrorResponse> accountExceptionHandler(AccountException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                             .body(new ApiErrorResponse(e.getErrorCode().getCode(), e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> securityExceptionHandler(SecurityException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                             .body(new ApiErrorResponse(e.getErrorCode().getCode(), e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(AiDiagnosisException.class)
    public ResponseEntity<ApiErrorResponse> securityExceptionHandler(AiDiagnosisException e) {
        log.error("MemberException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                             .body(new ApiErrorResponse(e.getErrorCode().getCode(), e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ApiErrorResponse> securityExceptionHandler(ReviewException e){
        log.error("ReviewException occurred: '{}'", e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                             .body(new ApiErrorResponse(e.getErrorCode().getCode(), e.getErrorCode(), e.getErrorCode().getMessage()));
    }

}
