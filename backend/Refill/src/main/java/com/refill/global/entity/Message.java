package com.refill.global.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Message {

    FIND_LOGIN_ID("가입하신 이메일로 로그인 아이디를 전송했습니다."),
    FIND_PASSWORD("가입하신 이메일로 임시 비밀번호를 전송했습니다.");

    private final String message;
}
