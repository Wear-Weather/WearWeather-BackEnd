package com.WearWeather.wear.domain.oauth.service;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.user.entity.Authority;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.jwt.TokenProvider;
import jakarta.transaction.Transactional;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(OAuthLoginParams params) {
        OAuthUserInfo oAuthUserInfo = requestOAuthInfoService.request(params);

        User user = userRepository.findByEmail(oAuthUserInfo.getEmail())
            .orElseGet(() -> registerNewUser(oAuthUserInfo));

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getEmail());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication.getName());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    protected User registerNewUser(OAuthUserInfo oAuthUserInfo) {

        Authority authority = Authority.builder()
            .authorityName("ROLE_USER")
            .build();

        User newUser = User.builder()
            .email(oAuthUserInfo.getEmail())
            .password(passwordEncoder.encode(oAuthUserInfo.getEmail()))
            .name(oAuthUserInfo.getName())
            .nickname(oAuthUserInfo.getNickname())
            .isSocial(true)
            .authorities(Collections.singleton(authority))
            .build();

        return userRepository.save(newUser);
    }
}
