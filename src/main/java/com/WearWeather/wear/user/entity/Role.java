package com.WearWeather.wear.user.entity;

public enum Role {
    USER("유저"), GUEST("비회원");

    private String value;

    Role(String value) {
        this.value = value;
    }
}

