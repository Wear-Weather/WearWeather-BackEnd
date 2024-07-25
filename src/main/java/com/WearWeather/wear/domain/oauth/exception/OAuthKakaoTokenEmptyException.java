package com.WearWeather.wear.domain.oauth.exception;

public class OAuthKakaoTokenEmptyException extends OAuthException {

    public OAuthKakaoTokenEmptyException() {
        super(OAuthErrorCode.KAKAO_TOKEN_EMPTY);
    }
}
