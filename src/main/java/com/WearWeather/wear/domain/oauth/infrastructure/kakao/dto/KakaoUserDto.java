package com.WearWeather.wear.domain.oauth.infrastructure.kakao.dto;

import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthProvider;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserDto implements OAuthUserInfo {

    private  Long id;
    private  Long userId;
    private  Long kakaoUserId;

    @Builder
    public KakaoUserDto(Long id, Long userId, Long kakaoUserId) {
        this.id = id;
        this.userId = userId;
        this.kakaoUserId = kakaoUserId;
    }

    public static KakaoUserDto of(KakaoUser kakaoUser) {
        return KakaoUserDto.builder()
            .id(kakaoUser.getId())
            .userId(kakaoUser.getUserId())
            .kakaoUserId(kakaoUser.getKakaoUserId())
            .build();
    }

    @Override
    public Long getId() {
        return kakaoUserId;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNickname() {
        return null;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }
}
