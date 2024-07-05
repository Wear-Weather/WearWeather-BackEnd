package com.WearWeather.wear.global.handler;


import com.WearWeather.wear.global.util.JWTUtil;
import com.WearWeather.wear.user.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(userDTO, 60);
        String refreshToken = jwtUtil.generateToken(userDTO, 60*24);

        Map<String, Object> claims = userDTO.getClaims();



        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);


        // Convert the claims map to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(claims);


        String encodedValue = URLEncoder.encode( jsonString, "UTF-8" ) ;
        Cookie cookie = new Cookie("auth_token", encodedValue);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS 환경에서만 사용
        cookie.setPath("/");
        cookie.setMaxAge((int) (24*60*60)); // 만료시간 설정 24시간



        response.addCookie(cookie);

        response.setStatus(HttpStatus.CREATED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(claims));
        response.getWriter().close();
    }
}
