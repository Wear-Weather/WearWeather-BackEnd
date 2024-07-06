package com.WearWeather.wear.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
public enum ErrorCode {

    //Auth
    EMAIL_IS_NULL_EXCEPTION(BAD_REQUEST, "이메일 값이 존재하지않습니다."),
    EMAIL_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 이메일 값 입니다."),
    PASSWORD_IS_NULL_EXCEPTION(BAD_REQUEST,"비밀번호 값이 존재하지않습니다."),
    PASSWORD_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 비밀번호 값 입니다."),
    NAME_IS_NULL_EXCEPTION(BAD_REQUEST,"이름 값이 존재하지않습니다."),
    NICKNAME_IS_NULL_EXCEPTION(BAD_REQUEST,"닉네임 값이 존재하지않습니다."),
    NICKNAME_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 닉네임 값 입니다."),

    NICKNAME_ALREADY_EXIST(BAD_REQUEST, "닉네임이 중복되었습니다."),
    NOT_EXIST_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    NOT_EXIST_USER(BAD_REQUEST, "일치하는 계정이 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
