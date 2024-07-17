package com.WearWeather.wear.user.repository;

import com.WearWeather.wear.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existByNickName(String nickname);
    boolean existByEmail(String email);

    Optional<User> findByNameAndNickname(String name, String nickname);
    boolean existByEmailAndNameAndNickname(String email, String name, String nickname);

}
