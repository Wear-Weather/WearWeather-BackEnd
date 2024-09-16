package com.WearWeather.wear.domain.oauth.infrastructure.kakao.repository;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {

    Optional<KakaoUser> findByUserId(Long UserId);

}
