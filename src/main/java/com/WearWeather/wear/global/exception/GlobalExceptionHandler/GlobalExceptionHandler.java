package com.WearWeather.wear.global.exception.GlobalExceptionHandler;

import com.WearWeather.wear.global.exception.CustomErrorResponse;
import com.WearWeather.wear.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String VALIDATION_ERROR = "VALIDATION_ERROR";

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
            ex.getHttpStatus().name(),
            ex.getErrorCode().name(),
            ex.getErrorMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<CustomErrorResponse> handleException(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        CustomErrorResponse errorResponse = new CustomErrorResponse(
            HttpStatus.BAD_REQUEST.name(),
            VALIDATION_ERROR,
            fieldError.getDefaultMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
