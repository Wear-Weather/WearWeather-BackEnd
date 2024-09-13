package com.WearWeather.wear.domain.oauth.infrastructure.kakao.repository;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {

    Optional<KakaoUser> findByKakaoUserId(Long kakaoUserId);

    @Query("SELECT k.kakaoUserId FROM KakaoUser k WHERE k.userId = :userId")
    Long findKakaoUserIdByUserId(@Param("userId") Long userId);


    void deleteByKakaoUserId(Long kakaoUserId);

}
