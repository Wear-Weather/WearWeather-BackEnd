package com.WearWeather.wear.user.exception;

import lombok.Getter;

@Getter
public enum UserException {
    EMAIL_IS_NULL_EXCEPTION("이메일 값이 존재하지않습니다."),
    EMAIL_INVALID_EXCEPTION("유효하지 않은 이메일 값 입니다."),
    PASSWORD_IS_NULL_EXCEPTION("비밀번호 값이 존재하지않습니다."),
    PASSWORD_INVALID_EXCEPTION("유효하지 않은 비밀번호 값 입니다."),
    NAME_IS_NULL_EXCEPTION("이름 값이 존재하지않습니다."),
    NICKNAME_IS_NULL_EXCEPTION("닉네임 값이 존재하지않습니다."),
    NICKNAME_INVALID_EXCEPTION("유효하지 않은 닉네임 값 입니다."),

    ;

    private final String message;

    UserException(String message){
        this.message = message;
    }
}