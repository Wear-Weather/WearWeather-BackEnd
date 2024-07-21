package com.WearWeather.wear.oauth.exception;

public class OAuthKakaoTokenEmptyException extends OAuthException {

    public OAuthKakaoTokenEmptyException() {
        super(OAuthErrorCode.KAKAO_TOKEN_EMPTY);
    }
}
