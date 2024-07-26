package com.WearWeather.wear.domain.user.entity;

import com.WearWeather.wear.global.common.BaseTimeEntity;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "`user`")
public class User extends BaseTimeEntity implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "is_social", nullable = false)
    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public void isRegularLogin() {
        this.isSocial = false;
    }

    public void isSocialLogin() {
        this.isSocial = true;

    }

    public void updatePassword(String password) {

        if (password == null || password.isEmpty()){
            throw new CustomException(ErrorCode.PASSWORD_INVALID_EXCEPTION);
        }

        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return email.equals(user.email)
            && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name);
    }
}


