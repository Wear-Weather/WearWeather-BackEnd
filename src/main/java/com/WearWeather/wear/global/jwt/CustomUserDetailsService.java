package com.WearWeather.wear.global.jwt;

import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
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
        User user = userRepository.findOneWithAuthoritiesByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException(userEmail + " -> 데이터베이스에서 찾을 수 없습니다."));

        return new CustomUserDetail(user);
    }

}
