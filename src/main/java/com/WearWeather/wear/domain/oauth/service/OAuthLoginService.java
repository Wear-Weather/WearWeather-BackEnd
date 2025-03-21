package com.WearWeather.wear.domain.oauth.service;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KaKaoUserInfo;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import com.WearWeather.wear.domain.user.entity.Authority;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.jwt.TokenProvider;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final KakaoUserService kakaoUserService;

    public TokenResponse login(OAuthLoginParams params) {
        OAuthUserInfo oAuthUserInfo = requestOAuthInfoService.request(params);
        User user = findOrRegisterUser(oAuthUserInfo);

        kakaoUserService.save((KaKaoUserInfo) oAuthUserInfo,user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
          user.getUserId(),
          null,
          user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
            .collect(Collectors.toList())
        );

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId());

        return TokenResponse.of(accessToken, refreshToken);
    }

    private User findOrRegisterUser(OAuthUserInfo oAuthUserInfo) {
        return userRepository.findByEmailAndIsDeleteFalseAndIsSocialTrue(oAuthUserInfo.getEmail())
            .map(user -> validateSocialUser(user, oAuthUserInfo))
            .orElseGet(() -> registerNewUser(oAuthUserInfo));
    }

    private User validateSocialUser(User existingUser, OAuthUserInfo oAuthUserInfo) {
        if (existingUser.isSocial()) {
            return existingUser;
        }
        return registerNewUser(oAuthUserInfo);
    }

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

    private Authentication authenticateUser(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getEmail());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}
