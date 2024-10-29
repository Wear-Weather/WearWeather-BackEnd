package com.WearWeather.wear.global.jwt;

import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String userEmail) {
        return userRepository.findOneWithAuthoritiesByEmailAndIsDeleteFalseAndIsSocialFalse(userEmail) //DB에서 유저 정보를 권한 정보와 함께 가져옴
            .map(user -> createUser(userEmail, user))
            .orElseThrow(() -> new UsernameNotFoundException(userEmail + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String userEmail, User user) {

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream() // 유저의 권한 정보들
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getUserId()),
            user.getPassword(),
            grantedAuthorities);
    }
}
