package com.WearWeather.wear.global.config;


import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.jwt.handler.JwtAccessDeniedHandler;
import com.WearWeather.wear.global.jwt.handler.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
        TokenProvider tokenProvider,
        CorsFilter corsFilter,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(AbstractHttpConfigurer::disable)

          .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
          .exceptionHandling(exceptionHandling -> exceptionHandling
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
          )

          .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/auth/login", "/auth/reissue", "/", "/oauth/kakao",
              "/login/page",
              "/users/nickname-check/**", "/email/send-verification", "/email/verify-code",
              "/users/register", "/users/email", "/users/password",
              "/basic-location", "/location/**", "/regions","/users/delete-reasons", "/weather/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/top-liked").permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/me").authenticated()

            .requestMatchers(HttpMethod.GET, "/posts").permitAll()
            .requestMatchers(HttpMethod.POST, "/posts/search").permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/{postId}").permitAll()
            .requestMatchers(HttpMethod.GET, "/posts/tmp").permitAll()

            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/docs/**").permitAll()
            .anyRequest().authenticated()
          )

          .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )

          .headers(headers ->
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
          )

          .with(new JwtSecurityConfig(tokenProvider), customizer -> {
          });
        return http.build();
    }
}
