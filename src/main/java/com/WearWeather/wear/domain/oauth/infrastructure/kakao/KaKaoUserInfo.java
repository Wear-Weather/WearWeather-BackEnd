package com.WearWeather.wear.domain.oauth.infrastructure.kakao;

import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthProvider;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KaKaoUserInfo implements OAuthUserInfo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    static class KakaoAccount {

        @JsonProperty("email")
        private String email;

        @JsonProperty("profile")
        private Profile profile;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter
        static class Profile {

            @JsonProperty("nickname")
            private String nickname;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    static class Properties {

        @JsonProperty("nickname")
        private String nickname;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Override
    public String getName() {
        // 카카오 계정에서 이름 정보는 직접 제공하지 않기 때문에 닉네임을 이름으로 사용
        return kakaoAccount.getProfile().getNickname();
    }

    @Override
    public String getNickname() {
        return properties.getNickname();
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }
}
