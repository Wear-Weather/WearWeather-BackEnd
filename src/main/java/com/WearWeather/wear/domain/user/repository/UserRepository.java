package com.WearWeather.wear.domain.user.repository;

import com.WearWeather.wear.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<User> findByNameAndNickname(String name, String nickname);

    boolean existsByEmailAndNameAndNickname(String email, String name, String nickname);

    Optional<User> findByEmail(String email);
}