package com.WearWeather.wear.domain.oauth.infrastructure.kakao.service;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KaKaoUserInfo;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.dto.KakaoUserDto;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.repository.KakaoUserRepository;
import com.WearWeather.wear.domain.oauth.service.RequestOAuthUnlinkService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final RequestOAuthUnlinkService requestOAuthUnlinkService;
    private final KakaoUserRepository kakaoUserRepository;

    @Transactional
    public void save(KaKaoUserInfo kakaoInfo, User user) {
        if (getKakaoUserByUserId(user.getUserId()).isEmpty()) {

            KakaoUser kakaoUser = KakaoUser.builder()
                .userId(user.getUserId())
                .kakaoUserId(kakaoInfo.getId())
                .build();

            kakaoUserRepository.save(kakaoUser);
        }
    }

    public Optional<KakaoUser> getKakaoUserByUserId(Long userId) {
        return kakaoUserRepository.findByUserId(userId);
    }

    public void deleteKakaoUser(KakaoUser kakaoUser) {
        kakaoUserRepository.delete(kakaoUser);
    }

    public void unlinkOauth(Long userId) {
        KakaoUser kakaoUser = getKakaoUserByUserId(userId)
          .orElseThrow(() -> new CustomException(ErrorCode.KAKAO_USER_NOT_FOUND));

        requestOAuthUnlinkService.request(KakaoUserDto.of(kakaoUser));
        deleteKakaoUser(kakaoUser);
    }
}
