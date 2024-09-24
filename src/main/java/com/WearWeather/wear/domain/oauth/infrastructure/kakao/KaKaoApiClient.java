package com.WearWeather.wear.domain.oauth.infrastructure.kakao;


import com.WearWeather.wear.domain.oauth.constants.OAuthConstant;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthClient;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthLoginParams;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthProvider;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.oauth.exception.OAuthKakaoTokenEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KaKaoApiClient implements OAuthClient {

    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth/token";
        HttpEntity<MultiValueMap<String, String>> request = generateHttpRequest(params);

        KaKaoToken kaKaoToken = restTemplate.postForObject(url, request, KaKaoToken.class);
        if (kaKaoToken == null || kaKaoToken.accessToken().isEmpty()) {
            throw new OAuthKakaoTokenEmptyException();
        }

        log.info(" [Kakao Service] Access Token ------> {}", kaKaoToken.accessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kaKaoToken.refreshToken());
        log.info(" [Kakao Service] Scope ------> {}", kaKaoToken.scope());

        return kaKaoToken.accessToken();
    }

    // 연결 끊기
    @Override
    public void unlinkOauthUser(Long kakaoUserId) {
        String url = apiUrl + "/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + adminKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", kakaoUserId.toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForObject(url, request, Void.class);
    }


    @Override
    public OAuthUserInfo requestOAuthInfo(String accessToken) {
        String url = apiUrl + "/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestToken = "Bearer " + accessToken;
        httpHeaders.set("Authorization", requestToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KaKaoUserInfo userInfo = restTemplate.postForObject(url, request, KaKaoUserInfo.class);

        log.info("[Kakao Service] User Info ------> {}", userInfo);

        return userInfo;
    }

    private HttpEntity<MultiValueMap<String, String>> generateHttpRequest(OAuthLoginParams params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", OAuthConstant.GRANT_TYPE);
        body.add("client_id", clientId);

        return new HttpEntity<>(body, httpHeaders);
    }
}
