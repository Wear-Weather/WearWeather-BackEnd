package com.WearWeather.wear.user.repository;

import com.WearWeather.wear.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    Optional<User> findByNameAndNickname(String name, String nickname);
    boolean existsByEmailAndNameAndNickname(String email, String name, String nickname);

    Optional<User> findByEmail(String email);
}
