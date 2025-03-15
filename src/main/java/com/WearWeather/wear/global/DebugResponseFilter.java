package com.WearWeather.wear.global;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        chain.doFilter(request, response);

        // 응답 헤더에서 Set-Cookie 확인
        Collection<String> cookies = httpResponse.getHeaders("Set-Cookie");
        if (cookies.isEmpty()) {
            log.warn("[DebugResponseFilter] Set-Cookie 헤더가 응답에 없음");
        } else {
            for (String cookie : cookies) {
                log.info("[DebugResponseFilter] Set-Cookie 헤더 포함: {}", cookie);
            }
        }
    }
}

