package com.WearWeather.wear.domain.oauth.domain.oauth;

public interface OAuthClient {

    OAuthProvider oauthProvider();

    String requestAccessToken(OAuthLoginParams params);

    OAuthUserInfo requestOAuthInfo(String accessToken);
}
