package com.WearWeather.wear.domain.oauth.domain.oauth;

public interface OAuthUserInfo {

    Long getId();

    String getEmail();

    String getName();

    String getNickname();

    OAuthProvider getOAuthProvider();
}
