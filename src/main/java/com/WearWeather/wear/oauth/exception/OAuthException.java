package com.WearWeather.wear.oauth.exception;


import com.WearWeather.wear.global.NewException.NewCustomException;
import com.WearWeather.wear.global.NewException.NewErrorCode;

public class OAuthException extends NewCustomException {

    public OAuthException(NewErrorCode errorCode) {
        super(errorCode);
    }
}
