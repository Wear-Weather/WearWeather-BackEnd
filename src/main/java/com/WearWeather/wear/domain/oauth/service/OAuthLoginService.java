package com.WearWeather.wear.domain.oauth.service;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.user.entity.Role;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

    private final UserRepository userRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final TokenProvider tokenProvider;

    public LoginResponse login(OAuthLoginParams params) {
        OAuthUserInfo oAuthUserInfo = requestOAuthInfoService.request(params);

        User user = userRepository.findByEmail(oAuthUserInfo.getEmail())
            .orElseGet(() -> registerNewUser(oAuthUserInfo));
        String accessToken = tokenProvider.createAccessToken(user.getEmail(), Role.USER);
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail(), Role.USER);

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    private User registerNewUser(OAuthUserInfo oAuthUserInfo) {
        User newUser = User.builder()
            .email(oAuthUserInfo.getEmail())
            .password(null)
            .name(oAuthUserInfo.getName())
            .nickname(oAuthUserInfo.getNickname())
            .isSocial(true)
            //            .provider(oAuthUserInfo.getOAuthProvider())
            .role(Role.USER)
            .build();

        return userRepository.save(newUser);
    }
}
