package com.WearWeather.wear.auth.dto.response;

import com.WearWeather.wear.auth.entity.Authority;
import com.WearWeather.wear.user.entity.User;
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
        private Long userId;
        private String email;
        private String username;
        private String nickName;
        private boolean isSocial;
        private Set<Authority> authorities;
        private String accessToken;
        private String refreshToken;

        public static LoginResponse of (User user, String atk, String rtk) {
            return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getName())
                .nickName(user.getNickname())
                .isSocial(user.isSocial())
                .authorities(user.getAuthorities())
                .accessToken(atk)
                .refreshToken(rtk)
                .build();
        }
    }
