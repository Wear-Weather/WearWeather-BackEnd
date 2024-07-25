package com.WearWeather.wear.domain.auth.dto.response;

import com.WearWeather.wear.domain.user.entity.Authority;
import com.WearWeather.wear.domain.user.entity.User;
import java.util.Set;
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
    private Set<Authority> authorities;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(User user, String at, String rt) {
        return LoginResponse.builder()
            .userId(user.getUserId())
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

