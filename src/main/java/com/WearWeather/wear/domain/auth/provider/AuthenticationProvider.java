package com.WearWeather.wear.domain.auth.provider;


import com.WearWeather.wear.domain.user.entity.User;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthenticationProvider(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public Authentication authenticateWithCredentials(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public Authentication createAuthenticatedToken(User user) {
        return new UsernamePasswordAuthenticationToken(
          user.getUserId(),
          null,
          user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
            .collect(Collectors.toList())
        );
    }
}
