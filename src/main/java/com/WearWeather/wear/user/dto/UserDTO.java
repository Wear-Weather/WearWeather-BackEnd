package com.WearWeather.wear.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements UserDetails{
    private long userId;
    private String username;
    private String nickname;
    private String password;
    private boolean isSocial;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public Map<String, Object> getClaims() {
        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        List<String> roles = authorities.stream().map(auth -> auth.getAuthority().substring(5)).collect(Collectors.toList());

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("username", username);
        dataMap.put("password", password);
        dataMap.put("nickname", nickname);
        dataMap.put("isSocial", isSocial);
        dataMap.put("roles", roles);

        return dataMap;
    }
}
