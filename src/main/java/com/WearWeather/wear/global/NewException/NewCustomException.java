package com.WearWeather.wear.global.NewException;

import lombok.Getter;

@Getter
public class NewCustomException extends RuntimeException {

    private final NewErrorCode errorCode;

    public NewCustomException(NewErrorCode errorCode) {
        super(errorCode.getErrorDetail());
        this.errorCode = errorCode;
    }
}
