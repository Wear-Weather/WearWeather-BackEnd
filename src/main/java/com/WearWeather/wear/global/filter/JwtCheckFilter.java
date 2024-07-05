package com.WearWeather.wear.global.filter;

import com.WearWeather.wear.global.service.CustomUserDetailsService;
import com.WearWeather.wear.global.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Log4j2
public class JwtCheckFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;

    private final JWTUtil jwtUtil;

    private static final String[] ALLOW_URL = {
            "/api/v1/auth/login",
            "/api/v1/auth/kakao-login",
            "/favicon.ico",
    };


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        final String url = request.getRequestURI();
        String method = request.getMethod();

        List<String> allow_url_matcher = Arrays.stream(ALLOW_URL).filter(x -> url.equals(x)).collect(Collectors.toList());

        if (allow_url_matcher.size() > 0 || "OPTIONS".equals(method.toUpperCase())) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = null;


        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token", e);
                setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Unable to get JWT Token");
                return;
            } catch (MalformedJwtException e) {
                log.error("Malformed JWT Token", e);
                setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Malformed JWT Token");
                return;
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT Token", e);
                setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT Token");
                return;
            } catch (InvalidClaimException e) {
                log.error("Invalid JWT Claims", e);
                setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Claims");
                return;
            } catch (JwtException e) {
                log.error("JWT Error", e);
                setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "JWT Error");
                return;
            } catch (Exception e) {
                log.error("JWT Parsing Error", e);
                setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "JWT Parsing Error");
                return;
            }


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        }
    }

    private void setErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", message);

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
    }
}
