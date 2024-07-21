package com.WearWeather.wear.oauth.domain.oauth;

public interface OAuthClient {

    OAuthProvider oauthProvider();

    String requestAccessToken(OAuthLoginParams params);

    OAuthUserInfo requestOAuthInfo(String accessToken);
}
