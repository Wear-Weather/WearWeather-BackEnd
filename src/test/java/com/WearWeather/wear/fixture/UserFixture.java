package com.WearWeather.wear.fixture;

import com.WearWeather.wear.user.entity.User;

public class UserFixture {

    public static User createUser(String email, String password) {
        return User.builder()
            .email(email)
            .password(password)
            .build();
    }

}
