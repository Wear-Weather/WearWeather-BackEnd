package com.WearWeather.wear.domain.user.facade;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.enums.DeleteReason;
import com.WearWeather.wear.domain.user.service.UserDeleteService;
import com.WearWeather.wear.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserDeleteFacade {

    private final UserService userService;
    private final UserDeleteService userDeleteService;
    private final KakaoUserService kakaoUserService;

    @Transactional
    public void deleteUser(Long userId, String reason) {
        DeleteReason deleteReason = userDeleteService.getDeleteReason(reason);
        User user = userService.softDelete(userId);
        userDeleteService.save(user, deleteReason);
        if (user.isSocial()) {
            kakaoUserService.unlinkOauth(userId);
        }
    }

}
