package com.WearWeather.wear.global.exception.GlobalExceptionHandler;

import com.WearWeather.wear.global.exception.CustomErrorResponse;
import com.WearWeather.wear.global.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
            ex.getHttpStatus().name(),
            ex.getErrorCode().name(),
            ex.getErrorMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

}
