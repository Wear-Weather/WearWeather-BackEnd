package com.WearWeather.wear.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
public enum ErrorCode {

    //Domain-user
    EMAIL_IS_NULL_EXCEPTION(BAD_REQUEST, "이메일 값이 존재하지않습니다."),
    EMAIL_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 이메일 값 입니다."),
    PASSWORD_IS_NULL_EXCEPTION(BAD_REQUEST,"비밀번호 값이 존재하지않습니다."),
    PASSWORD_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 비밀번호 값 입니다."),
    NAME_IS_NULL_EXCEPTION(BAD_REQUEST,"이름 값이 존재하지않습니다."),
    NICKNAME_IS_NULL_EXCEPTION(BAD_REQUEST,"닉네임 값이 존재하지않습니다."),
    NICKNAME_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 닉네임 값 입니다."),

    //User
    NICKNAME_ALREADY_EXIST(BAD_REQUEST, "닉네임이 중복되었습니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST, "이메일이 중복되었습니다."),
    NOT_EXIST_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    NOT_EXIST_USER(BAD_REQUEST, "일치하는 계정이 없습니다."),
    NO_SUCH_ALGORITHM(BAD_REQUEST, "해당 알고리즘이 존재하지 않습니다."),

    UNABLE_TO_SEND_EMAIL(BAD_REQUEST,"해당 이메일로 코드를 발송하지 못했습니다."),
    FAIL_EMAIL_VERIFICATION(BAD_REQUEST, "이메일 검증이 실패하였습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
