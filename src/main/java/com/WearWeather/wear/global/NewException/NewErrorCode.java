package com.WearWeather.wear.global.NewException;

import org.springframework.http.HttpStatus;

public interface NewErrorCode {

    String name();

    HttpStatus getHttpStatus();

    String getErrorDetail();
}
