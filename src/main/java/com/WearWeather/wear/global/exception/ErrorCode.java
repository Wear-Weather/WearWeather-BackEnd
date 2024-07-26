package com.WearWeather.wear.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Getter
public enum ErrorCode {

    //User
    EMAIL_IS_NULL_EXCEPTION(BAD_REQUEST, "이메일 값이 존재하지않습니다."),
    NICKNAME_ALREADY_EXIST(BAD_REQUEST, "닉네임이 중복되었습니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST, "이메일이 중복되었습니다."),
    NOT_EXIST_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    PASSWORD_INVALID_EXCEPTION(BAD_REQUEST,"유효하지 않은 비밀번호 값 입니다."),
    NOT_EXIST_USER(BAD_REQUEST, "일치하는 계정이 없습니다."),
    NOT_MATCH_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    NO_SUCH_ALGORITHM(BAD_REQUEST, "해당 알고리즘이 존재하지 않습니다."),

    UNABLE_TO_SEND_EMAIL(BAD_REQUEST,"해당 이메일로 코드를 발송하지 못했습니다."),
    FAIL_EMAIL_VERIFICATION(BAD_REQUEST, "이메일 검증이 실패하였습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST,"리프레시 토큰 값이 일치하지 않습니다."),

    REDIS_VALUE_NOT_FOUND(NOT_FOUND,"Redis에 저장된 값을 찾을 수 없습니다."),

    FAIL_UPDATE_PASSWORD(BAD_REQUEST, "비밀번호 수정 실패하였습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
