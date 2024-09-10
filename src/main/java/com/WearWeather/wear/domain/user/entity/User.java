package com.WearWeather.wear.domain.user.entity;

import com.WearWeather.wear.global.common.BaseTimeEntity;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
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

    @Builder.Default
    @Column(name = "is_delete", nullable = false)
    private boolean isDelete = false;

    @ManyToMany
    @JoinTable(
        name = "user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public void isRegularLogin() {
        this.isSocial = false;
    }

    public void isSocialLogin() {
        this.isSocial = true;
    }

    public void updatePassword(String password, boolean isSocial) {

        if(isSocial){
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_CANNOT_BE_MODIFIED);
        }

        if (password == null || password.isEmpty()){
            throw new CustomException(ErrorCode.PASSWORD_INVALID_EXCEPTION);
        }

        this.password = password;
    }

    public void updateNickname(String nickname) {

        if (nickname == null || nickname.isEmpty()){
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
        }

        this.nickname = nickname;
    }

    public void updateUserInfo(String password, String nickname, boolean isSocial) {

        updatePassword(password, isSocial);
        updateNickname(nickname);
    }

    public void updateIsDelete(){
        this.isDelete = true;
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