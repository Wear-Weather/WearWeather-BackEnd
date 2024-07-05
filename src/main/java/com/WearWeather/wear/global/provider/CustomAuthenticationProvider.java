package com.WearWeather.wear.global.provider;

import com.WearWeather.wear.global.service.CustomUserDetailsService;
import com.WearWeather.wear.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDTO userDTO = (UserDTO) userDetailsService.loadUserByUsername(username);

         if(!passwordEncoder.matches(password, userDTO.getPassword())){
            // 인증 실패 시 AuthenticationException 발생
            throw new BadCredentialsException("Authentication failed for " + username);
        }

        return new UsernamePasswordAuthenticationToken(userDTO, null, userDTO.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
