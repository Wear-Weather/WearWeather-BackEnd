package com.WearWeather.wear.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("정상 테스트 : 회원가입 테스트")
    public void registerUserTest() {

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        Optional<User> saveUser = userRepository.findByEmail(UserFixture.email);

        assertNotNull(saveUser);
        assertThat(saveUser.get().getNickname()).isEqualTo(UserFixture.nickname);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 이메일 인증하기")
    public void registerWithExistenEmailTest() {

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.checkDuplicatedUserEmail(UserFixture.email))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXIST);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 닉네임 확인하기")
    public void registerWithExistentNicknameTest() {

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.checkDuplicatedUserNickName(UserFixture.nickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXIST);

    }

    @Test
    @DisplayName("정상 테스트 : 아이디 찾기")
    public void findEmailTest() {

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

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

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.findUserPassword(UserFixture.email, UserFixture.name, UserFixture.nickname))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_USER);

    }

    @Test
    @DisplayName("정상 테스트 : 비밀번호 변경 완료")
    public void modifyPasswordTest() {

        String newPassword = "newPassword";

        User user = mock(User.class);

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(newPassword);

        userService.modifyPassword(UserFixture.email, UserFixture.password);

        verify(user).updatePassword(newPassword, UserFixture.isSocial);

    }

    @Test
    @DisplayName("예외 테스트 : 비밀번호 변경 시 올바르지 않은 비밀번호")
    public void modifyPasswordWithInvalidPasswordTest() {

        String newPassword = "newPassword";

        User user = mock(User.class);

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(newPassword);

        doThrow(new CustomException(ErrorCode.FAIL_UPDATE_PASSWORD))
                .when(user).updatePassword(anyString(), anyBoolean());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.modifyPassword(UserFixture.email, UserFixture.password));
        assertEquals(ErrorCode.FAIL_UPDATE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트 : 카카오 로그인 사용자는 비밀번호 변경 불가")
    public void modifyPasswordWithKakaoLoginUserTest() {

        User user = mock(User.class);

        when(user.isSocial()).thenReturn(true);
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.password);
        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));

        doThrow(new CustomException(ErrorCode.SOCIAL_ACCOUNT_CANNOT_BE_MODIFIED))
                .when(user).updatePassword(anyString(), eq(true));

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.modifyPassword(UserFixture.email, UserFixture.password));

        assertEquals(ErrorCode.FAIL_UPDATE_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 회원 정보 조회")
    public void getUserInfoTest() {

        User user = UserFixture.createUser();
        UserInfoResponse response = new UserInfoResponse(UserFixture.email, UserFixture.name, UserFixture.nickname);

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));

        UserInfoResponse actualResponse = userService.getUserInfo(UserFixture.email);

        assertEquals(response.getNickname(), actualResponse.getNickname());
        assertEquals(response.getNickname(), actualResponse.getNickname());

    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 조회 시 존재하지 않는 이메일")
    public void getUserInfoNonexistentEmailTest() {

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.getUserInfo(UserFixture.email));
        assertEquals(ErrorCode.NOT_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 회원 정보 수정")
    public void modifyUserInfoTest() {


        User user = mock(User.class);
        UserInfoResponse response = new UserInfoResponse(UserFixture.email, UserFixture.name, UserFixture.nickname);

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.password);

        userService.modifyUserInfo(UserFixture.email, UserFixture.password, UserFixture.nickname);

        verify(user).updateUserInfo(UserFixture.password, UserFixture.nickname, UserFixture.isSocial);

    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 수정 시 존재하지 않는 이메일")
    public void modifyUserInfoNonexistentEmailTest() {

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.modifyUserInfo(UserFixture.email, UserFixture.password, UserFixture.nickname));
        assertEquals(ErrorCode.NOT_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트 : 회원 정보 수정 시 유효하지 않은 닉네임")
    public void modifyUserInfoWithInvalidNicknameTest() {

        User user = mock(User.class);

        when(userRepository.findByEmail(UserFixture.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(UserFixture.password)).thenReturn(UserFixture.password);

        doThrow(new CustomException(ErrorCode.INVALID_NICKNAME))
                .when(user).updateUserInfo(anyString(), anyString(), anyBoolean());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.modifyUserInfo(UserFixture.email, UserFixture.password, UserFixture.nickname));

        assertEquals(ErrorCode.FAIL_UPDATE_USER_INFO, exception.getErrorCode());

    }
}