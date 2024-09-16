package com.WearWeather.wear.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.dto.KakaoUserDto;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import com.WearWeather.wear.domain.oauth.service.RequestOAuthUnlinkService;
import com.WearWeather.wear.domain.user.dto.request.DeleteReasonRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.enums.DeleteReason;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.domain.user.service.UserDeleteService;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserDeleteService userDeleteService;

    @Mock
    KakaoUserService kakaoUserService;

    @Mock
    RequestOAuthUnlinkService requestOAuthUnlinkService;

    @Mock
    KakaoUser kakaoUser;

    @Mock
    KakaoUserDto kakaoUserDto;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("정상 테스트 : 회원가입 테스트")
    public void registerUserTest() {

        RegisterUserRequest request = mock(RegisterUserRequest.class);
        User user = mock(User.class);

        when(request.getPassword()).thenReturn(UserFixture.password);
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.encodedPassword);
        when(request.toEntity(UserFixture.encodedPassword)).thenReturn(user);

        userService.registerUser(request);

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 이메일 인증하기")
    public void registerWithExistenEmailTest() {

        User user = mock(User.class);
        when(userRepository.existsByEmailAndIsDeleteFalse(UserFixture.email)).thenReturn(true);

        assertThatThrownBy(() -> userService.checkDuplicatedUserEmail(UserFixture.email))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXIST);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 닉네임 확인하기")
    public void registerWithExistentNicknameTest() {

        when(userRepository.existsByNicknameAndIsDeleteFalse(UserFixture.nickname)).thenReturn(true);

        assertThatThrownBy(() -> userService.checkDuplicatedUserNickName(UserFixture.nickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXIST);

    }

    @Test
    @DisplayName("정상 테스트 : 아이디 찾기")
    public void findEmailTest() {

        User user = UserFixture.createUser();
        when(userRepository.findByNameAndNicknameAndIsDeleteFalse(UserFixture.name, UserFixture.nickname)).thenReturn(Optional.of(user));

        String email = userService.findUserEmail(UserFixture.name, UserFixture.nickname);

        assertNotNull(email);
        assertThat(email).isEqualTo(UserFixture.email);
    }

    @Test
    @DisplayName("예외 테스트 : 아이디 찾기 시 일치하는 정보가 없을 때")
    public void findEmailNotMatchRequestTest() {

        assertThatThrownBy(() -> userService.findUserEmail(UserFixture.name, UserFixture.nickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_EMAIL);

    }

    @Test
    @DisplayName("예외 테스트 : 비밀번호 찾기 시 일치하는 정보가 없을 때")
    public void findPasswordNotMatchRequestTest() {

        when(userRepository.findByEmailAndNameAndNicknameAndIsDeleteFalse(UserFixture.email, UserFixture.name, UserFixture.nickname)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserPassword(UserFixture.email, UserFixture.name, UserFixture.nickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_USER);

    }

    @Test
    @DisplayName("정상 테스트 : 비밀번호 변경 완료")
    public void modifyPasswordTest() {
        Long userId = 1L;
        String newPassword = "newPassword";

        ModifyUserPasswordRequest request = new ModifyUserPasswordRequest(userId, newPassword);
        User user = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        userService.modifyPassword(request);

        verify(user).updatePassword(newPassword, UserFixture.isSocial);

    }

    @Test
    @DisplayName("예외 테스트 : 비밀번호 변경 시 올바르지 않은 비밀번호")
    public void modifyPasswordWithInvalidPasswordTest() {
        Long userId = 1L;
        String newPassword = "newPassword";

        ModifyUserPasswordRequest request = new ModifyUserPasswordRequest(userId, newPassword);

        User user = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        doThrow(new CustomException(ErrorCode.FAIL_UPDATE_PASSWORD))
            .when(user).updatePassword(anyString(), anyBoolean());

        CustomException exception = assertThrows(CustomException.class, () ->
            userService.modifyPassword(request));
        assertEquals(ErrorCode.FAIL_UPDATE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트 : 카카오 로그인 사용자는 비밀번호 변경 불가")
    public void modifyPasswordWithKakaoLoginUserTest() {

        Long userId = 1L;
        String newPassword = "newPassword";

        ModifyUserPasswordRequest request = new ModifyUserPasswordRequest(userId, newPassword);

        User user = mock(User.class);

        when(user.isSocial()).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(UserFixture.password);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doThrow(new CustomException(ErrorCode.SOCIAL_ACCOUNT_CANNOT_BE_MODIFIED))
            .when(user).updatePassword(anyString(), eq(true));

        CustomException exception = assertThrows(CustomException.class, () ->
            userService.modifyPassword(request));

        assertEquals(ErrorCode.FAIL_UPDATE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 회원 정보 조회")
    public void getUserInfoTest() {

        User user = UserFixture.createUser();
        UserInfoResponse response = new UserInfoResponse(UserFixture.email, UserFixture.name, UserFixture.nickname);

        when(userRepository.findByUserIdAndIsDeleteFalse(UserFixture.userId)).thenReturn(Optional.of(user));

        UserInfoResponse actualResponse = userService.getUserInfo(UserFixture.userId);

        assertEquals(response.getNickname(), actualResponse.getNickname());
        assertEquals(response.getNickname(), actualResponse.getNickname());

    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 조회 시 존재하지 않는 이메일")
    public void getUserInfoNonexistentEmailTest() {

        when(userRepository.findByUserIdAndIsDeleteFalse(UserFixture.userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
            userService.getUserInfo(UserFixture.userId));
        assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 회원 정보 수정")
    public void modifyUserInfoTest() {

        User user = mock(User.class);

        when(userRepository.findByUserIdAndIsDeleteFalse(UserFixture.userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.password);

        userService.modifyUserInfo(UserFixture.userId, UserFixture.password, UserFixture.nickname);

        verify(user).updateUserInfo(UserFixture.password, UserFixture.nickname, UserFixture.isSocial);

    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 수정 시 존재하지 않는 이메일")
    public void modifyUserInfoNonexistentEmailTest() {

        when(userRepository.findByUserIdAndIsDeleteFalse(UserFixture.userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
            userService.modifyUserInfo(UserFixture.userId, UserFixture.password, UserFixture.nickname));
        assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 수정 시 유효하지 않은 닉네임")
    public void modifyUserInfoWithInvalidNicknameTest() {

        User user = mock(User.class);

        when(userRepository.findByUserIdAndIsDeleteFalse(UserFixture.userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.password);

        doThrow(new CustomException(ErrorCode.INVALID_NICKNAME))
            .when(user).updateUserInfo(anyString(), anyString(), anyBoolean());

        CustomException exception = assertThrows(CustomException.class, () ->
            userService.modifyUserInfo(UserFixture.userId, UserFixture.password, UserFixture.nickname));

        assertEquals(ErrorCode.FAIL_UPDATE_USER_INFO, exception.getErrorCode());

    }

    @Test
    @DisplayName("정상 테스트 : 일반 사용자가 회원 탈퇴에 성공한다")
    public void deleteUserSuccessTest() {
        // given
        Long userId = 1L;
        String deleteReason = "오류가 잦아요";
        DeleteReasonRequest request = new DeleteReasonRequest(deleteReason);

        User user = mock(User.class);
        DeleteReason enumReason = DeleteReason.ERROR_FREQUENT;

        when(userRepository.findByUserIdAndIsDeleteFalse(userId)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(userId, request);

        // then
        verify(user).updateIsDelete();
        verify(userDeleteService).save(user, enumReason);
    }

    @Test
    @DisplayName("예외 테스트 : 존재하지 않는 사용자 정보로 회원 탈퇴를 시도하여 실패한다.")
    public void deleteUserNotFoundTest() {
        // given
        Long userId = 1L;
        String deleteReason = "오류가 잦아요";
        DeleteReasonRequest request = new DeleteReasonRequest(deleteReason);

        when(userRepository.findByUserIdAndIsDeleteFalse(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_USER);
    }

    @Test
    @DisplayName("예외 테스트 : 유효하지 않은 탈퇴 이유 값으로 회원 탈퇴를 시도하여 실패한다.")
    public void deleteUserWithInvalidReasonTest() {
        // given
        Long userId = 1L;

        String invalidReason = "유효하지 않은 탈퇴 이유";
        DeleteReasonRequest request = new DeleteReasonRequest(invalidReason);

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DELETE_REASON);
    }

    @Test
    @DisplayName("정상 테스트 : 소셜 로그인 사용자가 회원 탈퇴를 성공한다")
    public void deleteSocialUserTest() {
        // given
        Long userId = 1L;
        String deleteReason = "서비스 기능이 미흡해요";
        DeleteReasonRequest request = new DeleteReasonRequest(deleteReason);

        User user = mock(User.class);
        DeleteReason enumReason = DeleteReason.POOR_FUNCTIONALITY;

        when(userRepository.findByUserIdAndIsDeleteFalse(userId)).thenReturn(Optional.of(user));
        when(user.isSocial()).thenReturn(true);
        when(kakaoUserService.getKakaoUserByUserId(userId)).thenReturn(Optional.of(kakaoUser));

        // when
        userService.deleteUser(userId, request);

        // then
        verify(user).updateIsDelete();
        verify(userDeleteService).save(user, enumReason);
        verify(kakaoUserService).deleteKakaoUser(kakaoUser);
    }

    @Test
    @DisplayName("예외 테스트 : 소셜 로그인 사용자 정보가 없어 회원 탈퇴에 실패한다")
    public void deleteSocialUserNotFoundTest() {
        // given
        Long userId = 1L;
        String deleteReason = "서비스 기능이 미흡해요";
        DeleteReasonRequest request = new DeleteReasonRequest(deleteReason);

        User user = mock(User.class);
        when(userRepository.findByUserIdAndIsDeleteFalse(userId)).thenReturn(Optional.of(user));
        when(user.isSocial()).thenReturn(true);
        when(kakaoUserService.getKakaoUserByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId, request))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.KAKAO_USER_NOT_FOUND);
    }
}
