package com.WearWeather.wear.auth.dto.response;

import com.WearWeather.wear.auth.dto.TokenInfo;
import com.WearWeather.wear.oauth.domain.oauth.OAuthProvider;
import com.WearWeather.wear.user.entity.Role;
import com.WearWeather.wear.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private long userId;
    private String email;
    private String name;
    private String nickName;
    private boolean isSocial;
    private Role role;
    private OAuthProvider provider;
    private TokenInfo tokenInfo;

    public static LoginResponse of(User user, TokenInfo tokenInfo) {
        return LoginResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .nickName(user.getNickname())
            .isSocial(user.isSocial())
            .role(user.getRole())
            .provider(user.getProvider())
            .tokenInfo(tokenInfo)
            .build();
    }
}

