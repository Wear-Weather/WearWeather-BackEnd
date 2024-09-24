package com.WearWeather.wear.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {

    //User
    EMAIL_IS_NULL_EXCEPTION(NOT_FOUND, "이메일이 존재하지 않습니다."),
    NICKNAME_ALREADY_EXIST(BAD_REQUEST, "닉네임이 중복되었습니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST, "이메일이 중복되었습니다."),
    NOT_EXIST_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    PASSWORD_INVALID_EXCEPTION(BAD_REQUEST, "유효하지 않은 비밀번호 값 입니다."),
    NOT_EXIST_USER(NOT_FOUND, "존재하지 않는 사용자 입니다."),
    NOT_MATCH_EMAIL(BAD_REQUEST, "일치하는 이메일이 없습니다."),
    NO_SUCH_ALGORITHM(BAD_REQUEST, "해당 알고리즘이 존재하지 않습니다."),

    UNABLE_TO_SEND_EMAIL(BAD_REQUEST, "해당 이메일로 코드를 발송하지 못했습니다."),
    FAIL_EMAIL_VERIFICATION(BAD_REQUEST, "이메일 검증이 실패하였습니다."),

    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "RefreshToekn이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, "AccessToken이 유효하지 않습니다."),
    KAKAO_LOGIN_FAIL(BAD_REQUEST, "카카오 로그인 실패"),
    REDIS_VALUE_NOT_FOUND(NOT_FOUND, "Redis에 저장된 값을 찾을 수 없습니다."),

    FAIL_UPDATE_PASSWORD(BAD_REQUEST, "비밀번호 수정 실패하였습니다."),
    INVALID_NICKNAME(BAD_REQUEST, "유효하지 않은 닉네임 값 입니다."),
    FAIL_UPDATE_USER_INFO(BAD_REQUEST, "회원정보 수정 실패하였습니다."),
    SOCIAL_ACCOUNT_CANNOT_BE_MODIFIED(BAD_REQUEST, "카카오 로그인 사용자는 비밀번호 수정이 불가합니다."),

    SERVER_ERROR(BAD_REQUEST, "이미지 업로드 실패"),
    INVALID_IMAGE_IMAGE(BAD_REQUEST, "이미지 파일이 유효하지 않습니다"),
    IMAGE_NOT_FOUND(NOT_FOUND, "이미지를 찾을 수 없습니다."),
    USED_IMAGE_IN_DIFFERENT_POST(BAD_REQUEST,"다른 게시글에서 사용되고 있는 이미지 ID가 있습니다. 이미지를 다시 업로드 해주세요"),

    NOT_EXIST_POST(BAD_REQUEST, "존재하지 않는 게시글입니다."),
    ALREADY_LIKED_POST(BAD_REQUEST, "이미 좋아요된 게시글입니다."),
    NOT_LIKED_POST(BAD_REQUEST, "좋아요한 게시글이 아닙니다."),
    TAG_NOT_FOUND(NOT_FOUND, "태그 아이디가 존재하지 않습니다."),
    NOT_EXIST_POST_IMAGE(BAD_REQUEST, "존재하지 않는 게시글 이미지입니다."),
    NOT_EXIST_CITY_ID(BAD_REQUEST, "일치하는 광역시도가 없습니다."),
    NOT_EXIST_DISTRICT_ID(BAD_REQUEST, "일치하는 시군구가 없습니다."),
    NOT_EXIST_CITY(BAD_REQUEST, "일치하는 광역시도가 없습니다."),
    NOT_EXIST_DISTRICT(BAD_REQUEST, "일치하는 시군구가 없습니다."),

    HIDDEN_POST_ALREADY_EXIST(BAD_REQUEST, "이미 숨겨진 게시물입니다."),

    REPORT_POST_ALREADY_EXIST(BAD_REQUEST, "이미 신고한 게시글입니다."),
    INVALID_REQUEST_PARAMETER(BAD_REQUEST, "경도, 위도 값이 유효하지 않습니다."),
    GEO_COORD_SERVER_ERROR(BAD_REQUEST,"서버 오류입니다."),
  
    ALREADY_DELETE_USER(BAD_REQUEST, "해당 사용자는 이미 탈퇴한 사용자입니다."),
    NOT_FOUND_DELETE_REASON(NOT_FOUND, "존재하지 않는 탈퇴 이유 ID 입니다."),

    KAKAO_USER_NOT_FOUND(BAD_REQUEST, "카카오 사용자 정보가 존재하지 않습니다."),
    INVALID_DELETE_REASON(BAD_REQUEST,"올바르지 않은 탈퇴 이유 값 입니다"),

    UNAUTHORIZED_USER(UNAUTHORIZED,"게시글 수정 권한이 없습니다."),
    IMAGE_ALREADY_USED(BAD_REQUEST, "이미 다른 게시글에 사용된 이미지ID가 있습니다."),

    FAIL_SAVE_LOCATION_CITY(BAD_REQUEST, "광역시도 저장 오류입니다."),
    FAIL_SAVE_LOCATION_DISTRICT(BAD_REQUEST, "시군구 저장 오류입니다."),
    FAIL_CREATE_LOCATION_ACCESS_TOKEN(BAD_REQUEST, "위치 액세스 토큰 생성 오류입니다.."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
