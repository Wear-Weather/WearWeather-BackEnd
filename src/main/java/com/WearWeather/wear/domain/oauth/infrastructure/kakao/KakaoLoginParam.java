package com.WearWeather.wear.domain.oauth.infrastructure.kakao;

import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginParam implements OAuthLoginParams {

    private String authorizationCode;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    // 카카오 서버에 보낼 요청 바디를 구성
    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
