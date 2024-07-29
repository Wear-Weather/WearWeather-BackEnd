package com.WearWeather.wear.domain.user.dto.request;

import com.WearWeather.wear.domain.user.entity.Authority;
import com.WearWeather.wear.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Collections;


@Getter
public class RegisterUserRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private final String password;

    @NotBlank(message = "이름은 필수입니다.")
    private final String name;

    @NotBlank(message = "닉네임은 필수입니다.")
    private final String nickname;

    private final boolean isSocial;

    @AssertTrue(message = "이메일 인증은 필수입니다.")
    private final boolean checkEmail;

    @AssertTrue(message = "닉네임 중복 확인은 필수입니다.")
    private final boolean checkNickname;

    @JsonCreator
    public RegisterUserRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("name") String name,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("isSocial") boolean isSocial,
            @JsonProperty("checkEmail") boolean checkEmail,
            @JsonProperty("checkNickname") boolean checkNickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.isSocial = isSocial;
        this.checkEmail = checkEmail;
        this.checkNickname = checkNickname;
    }

    public User toEntity(String encodePassword){

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return User.builder()
                .email(email)
                .password(encodePassword)
                .name(name)
                .nickname(nickname)
                .isSocial(isSocial)
                .authorities(Collections.singleton(authority))
                .build();
    }
}
