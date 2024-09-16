package com.WearWeather.wear.domain.user.repository;

import com.WearWeather.wear.domain.user.entity.UserDelete;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserDeleteRepository extends JpaRepository<UserDelete, Long> {

    Optional<UserDelete> findByUserId(Long userId);

}
