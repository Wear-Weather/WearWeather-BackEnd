package com.WearWeather.wear.global.service;

import com.WearWeather.wear.user.dto.UserDTO;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserNamePasswordAuthenticationFilter에서 유저의 username과 password를  매칭한후
 * DB에 저장되어 있는 유저를 반환한다.
 */
@RequiredArgsConstructor
@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }
        User user = result.get();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .isSocial(user.isSocial())
                .authorities(authorities)
                .build();
    }
}
