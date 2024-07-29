package com.WearWeather.wear.global.jwt.handler;

import com.WearWeather.wear.global.exception.CustomErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CustomErrorResponse customErrorResponse;

        if (authException instanceof BadCredentialsException) {
            customErrorResponse = new CustomErrorResponse(
                HttpStatus.UNAUTHORIZED.name(),
                "INVALID_PASSWORD",
                "잘못된 비밀번호입니다."
            );
        } else {
            customErrorResponse = new CustomErrorResponse(
                HttpStatus.UNAUTHORIZED.name(),
                "INVALID_CREDENTIALS",
                "인증에 실패했습니다."
            );
        }

        String errorMessage = objectMapper.writeValueAsString(customErrorResponse);
        response.getWriter().write(errorMessage);
    }
}
