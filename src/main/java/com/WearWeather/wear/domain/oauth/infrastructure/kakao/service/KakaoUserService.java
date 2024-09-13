package com.WearWeather.wear.domain.oauth.infrastructure.kakao.service;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.repository.KakaoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final KakaoUserRepository kakaoUserRepository;

    @Transactional
    public void saveUser(Long kakaoUserId, Long userId) {
        if (kakaoUserRepository.findByKakaoUserId(kakaoUserId).isEmpty()) {

            KakaoUser kakaoUser = KakaoUser.builder()
                .userId(userId)
                .kakaoUserId(kakaoUserId)
                .build();

            kakaoUserRepository.save(kakaoUser);
        }
    }
}
