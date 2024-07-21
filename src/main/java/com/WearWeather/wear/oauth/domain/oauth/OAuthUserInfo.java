package com.WearWeather.wear.oauth.domain.oauth;

public interface OAuthUserInfo {

    String getEmail();

    String getName();

    String getNickname();

    OAuthProvider getOAuthProvider();
}
