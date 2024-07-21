package com.WearWeather.wear.oauth.service;

import com.WearWeather.wear.auth.dto.TokenInfo;
import com.WearWeather.wear.auth.dto.response.LoginResponse;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.user.entity.Role;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
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
        TokenInfo tokenInfo = tokenProvider.createToken2(user.getEmail(), Role.USER);

        return LoginResponse.of(user, tokenInfo);
    }

    private User registerNewUser(OAuthUserInfo oAuthUserInfo) {
        User newUser = User.builder()
            .email(oAuthUserInfo.getEmail())
            .password(null)
            .name(oAuthUserInfo.getName())
            .nickname(oAuthUserInfo.getNickname())
            .isSocial(true)
            .provider(oAuthUserInfo.getOAuthProvider())
            .role(Role.USER)
            .build();

        return userRepository.save(newUser);
    }
}
