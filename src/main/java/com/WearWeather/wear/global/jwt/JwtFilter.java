package com.WearWeather.wear.global.jwt;

import com.WearWeather.wear.global.exception.CustomErrorResponse;
import com.WearWeather.wear.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;
    public static final String USERS_REISSUE = "/users/reissue";

    // 토큰의 인증정보(ID, 권한 정보 등)를 SecurityContext에 저장
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse; // 응답 객체 가져오기

        String requestURI = httpServletRequest.getRequestURI();
        // 특정 API 제외
        if (USERS_REISSUE.equals(requestURI)) {
            logger.debug("JWT 필터를 건너뜀: {}", requestURI);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String jwt = resolveToken(httpServletRequest);
        try {
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {
                logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (CustomException e) {
            logger.error("예외 발생: {}", e.getErrorMessage());
            setErrorResponse(httpServletResponse, e);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, CustomException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CustomErrorResponse errorResponse = new CustomErrorResponse(
            e.getHttpStatus().name(),
            e.getErrorCode().name(),
            e.getErrorMessage()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonErrorResponse);
    }
}
