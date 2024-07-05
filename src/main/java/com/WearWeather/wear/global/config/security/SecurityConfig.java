package com.WearWeather.wear.global.config.security;


import com.WearWeather.wear.global.filter.JwtCheckFilter;
import com.WearWeather.wear.global.handler.LoginFailureHandler;
import com.WearWeather.wear.global.handler.LoginSuccessHandler;
import com.WearWeather.wear.global.provider.CustomAuthenticationProvider;
import com.WearWeather.wear.global.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final CustomAuthenticationProvider authenticationProvider;
    private final JwtCheckFilter jwtCheckFilter;



    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .authenticationProvider(authenticationProvider)
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin
                        .loginPage("/api/v1/auth/login")                        //로그인 api
                        .successHandler(new LoginSuccessHandler(jwtUtil))       //로그인 성공,실패시 handler로 응답
                        .failureHandler(new LoginFailureHandler())
                        .permitAll())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/index.html").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/img/**", "/image/**", "/images/**").permitAll()
                        .requestMatchers("/font/**", "/fonts/**").permitAll()
                        .requestMatchers("/file/**", "/files/**").permitAll()
                        .requestMatchers("/vendor/**").permitAll()
                        .requestMatchers("/configuration/**", "/api-docs/**", "/v3/api-docs/**", "/swagger*/**").permitAll() // springdoc, swagger
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/auth/kakao-login").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll() // 특정 URL에 대해 필터 제외
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000/"));            //리액트 기본포트
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}