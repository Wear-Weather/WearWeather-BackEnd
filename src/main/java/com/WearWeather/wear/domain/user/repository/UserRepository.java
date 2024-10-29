package com.WearWeather.wear.domain.user.repository;

import com.WearWeather.wear.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailAndIsDeleteFalse(String email);

    boolean existsByNicknameAndIsDeleteFalse(String nickname);

    boolean existsByEmailAndIsDeleteFalse(String email);
    Optional<User> findByNameAndNicknameAndIsDeleteFalse(String name, String nickname);

    Optional<User> findByEmailAndNameAndNicknameAndIsDeleteFalse(String email, String name, String nickname);

    Optional<User> findByEmailAndIsDeleteFalseAndIsSocialTrue(String email);

    Optional<UserNicknameMapping> findNicknameByUserIdAndIsDeleteFalse(Long userId);
    Optional<User> findByUserIdAndIsDeleteFalse(Long userId);

}
