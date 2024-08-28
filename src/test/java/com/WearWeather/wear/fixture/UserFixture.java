package com.WearWeather.wear.fixture;

import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.entity.User;

public class UserFixture {

    public static final Long userId = 1L;
    public static final String email = "abcd@gmail.com";
    public static final String password = "abcd12!@";
    public static final String encodedPassword = "encodedPassword";
    public static final String name = "스프링";
    public static final String nickname = "날씨웨어";
    public static final boolean isSocial = false;

    public static User createUser(String email, String password) {
        return User.builder()
            .email(email)
            .password(password)
            .build();
    }

    public static User createUser() {
        return User.builder()
            .userId(userId)
            .email(email)
            .password(encodedPassword)
            .name(name)
            .nickname(nickname)
            .isSocial(false)
            .build();
    }

    public static RegisterUserRequest createRegisterUserRequest() {

        return new RegisterUserRequest(email, password, name, nickname, isSocial);

    }
}
