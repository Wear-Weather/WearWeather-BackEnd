package com.WearWeather.wear.domain.oauth.service;

import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthClient;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthProvider;
import com.WearWeather.wear.domain.oauth.domain.oauth.OAuthUserInfo;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RequestOAuthUnlinkService {

    private final Map<OAuthProvider, OAuthClient> clients;

    public RequestOAuthUnlinkService(List<OAuthClient> clients) {
        this.clients = clients.stream().collect(
            Collectors.toUnmodifiableMap(OAuthClient::oauthProvider, Function.identity())
        );
    }

    public void request(OAuthUserInfo oAuthUserInfo) {
        OAuthClient client = clients.get(oAuthUserInfo.getOAuthProvider());
        client.unlinkOauthUser(oAuthUserInfo.getId());
    }
}
