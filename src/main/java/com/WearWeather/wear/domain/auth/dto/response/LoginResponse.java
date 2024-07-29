package com.WearWeather.wear.domain.auth.dto.response;

import com.WearWeather.wear.domain.user.entity.Authority;
import com.WearWeather.wear.domain.user.entity.User;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private final String name;
    private final String email;
    private final String nickName;
    private final boolean isSocial;
    private final Set<Authority> authorities;
    private final String accessToken;
    private final String refreshToken;

    public static LoginResponse of(User user, String at, String rt) {
        return LoginResponse.builder()
            .email(user.getEmail())
            .name(user.getName())
            .nickName(user.getNickname())
            .isSocial(user.isSocial())
            .authorities(user.getAuthorities())
            .accessToken(at)
            .refreshToken(rt)
            .build();
    }
}

