package com.WearWeather.wear.domain.user.entity;

public enum Role {
    USER("유저"),
    ADMIN("관리자");

    private String value;

    Role(String value) {
        this.value = value;
    }
}

