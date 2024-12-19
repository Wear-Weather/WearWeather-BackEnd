package com.WearWeather.wear.domain.user.facade;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.enums.DeleteReason;
import com.WearWeather.wear.domain.user.service.UserDeleteService;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("UserDeleteFacade 테스트")
@ExtendWith(MockitoExtension.class)
public class UserDeleteFacadeTest {

    @InjectMocks
    UserDeleteFacade userDeleteFacade;

    @Mock
    UserService userService;

    @Mock
    UserDeleteService userDeleteService;

    @Mock
    KakaoUserService kakaoUserService;

    @Mock
    User user;

    @Test
    @DisplayName("정상 테스트: 일반 사용자가 회원 탈퇴에 성공한다")
    public void deleteUserSuccessTest() {
        // given
        Long userId = 1L;
        String deleteReason = "오류가 잦아요";
        DeleteReason enumReason = DeleteReason.ERROR_FREQUENT;

        when(userService.softDelete(userId)).thenReturn(user);
        when(userDeleteService.getDeleteReason(deleteReason)).thenReturn(enumReason);

        // when
        userDeleteFacade.deleteUser(userId, deleteReason);

        // then
        verify(userService).softDelete(userId);
        verify(userDeleteService).save(user, enumReason);
    }

    @Test
    @DisplayName("예외 테스트: 존재하지 않는 사용자 정보로 회원 탈퇴를 시도하여 실패한다.")
    public void deleteUserNotFoundTest() {
        // given
        Long userId = 1L;
        String deleteReason = "오류가 잦아요";

        when(userService.softDelete(userId))
          .thenThrow(new CustomException(ErrorCode.NOT_EXIST_USER));

        // when & then
        assertThatThrownBy(() -> userDeleteFacade.deleteUser(userId, deleteReason))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_USER);
    }

    @Test
    @DisplayName("예외 테스트: 유효하지 않은 탈퇴 이유 값으로 회원 탈퇴를 시도하여 실패한다.")
    public void deleteUserWithInvalidReasonTest() {
        // given
        Long userId = 1L;
        String invalidReason = "유효하지 않은 탈퇴 이유";

        when(userDeleteService.getDeleteReason(invalidReason))
          .thenThrow(new CustomException(ErrorCode.INVALID_DELETE_REASON));

        // when & then
        assertThatThrownBy(() -> userDeleteFacade.deleteUser(userId, invalidReason))
          .isInstanceOf(CustomException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DELETE_REASON);
    }

    @Test
    @DisplayName("정상 테스트: 소셜 로그인 사용자가 회원 탈퇴에 성공한다")
    public void deleteSocialUserTest() {
        // given
        Long userId = 1L;
        String deleteReason = "서비스 기능이 미흡해요";
        DeleteReason enumReason = DeleteReason.POOR_FUNCTIONALITY;

        when(userService.softDelete(userId)).thenReturn(user);
        when(userDeleteService.getDeleteReason(deleteReason)).thenReturn(enumReason);
        when(user.isSocial()).thenReturn(true);

        // when
        userDeleteFacade.deleteUser(userId, deleteReason);

        // then
        verify(userService).softDelete(userId);
        verify(userDeleteService).save(user, enumReason);
        verify(kakaoUserService).unlinkOauth(userId);
    }
}
