package com.WearWeather.wear.user.entity;

import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
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
}


