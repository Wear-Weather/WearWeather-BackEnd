package com.WearWeather.wear.user.service;

import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("MemberService 테스트")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("정상 테스트 : 회원가입 테스트")
    public void registerUserTest(){

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        Optional<User> saveUser = userRepository.findByEmail(UserFixture.email);

        assertNotNull(saveUser);
        assertThat(saveUser.get().getNickname()).isEqualTo(UserFixture.nickname);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 이메일 인증하기")
    public void registerWithExistenEmailTest(){

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.checkDuplicatedUserEmail(UserFixture.email))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXIST);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 존재하는 닉네임 확인하기")
    public void registerWithExistentNicknameTest(){

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.checkDuplicatedUserNickName(UserFixture.nickname))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_ALREADY_EXIST);

    }

    @Test
    @DisplayName("정상 테스트 : 아이디 찾기")
    public void findEmailTest(){

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        String email = userService.findUserEmail(UserFixture.name, UserFixture.nickname);

        assertNotNull(email);
        assertThat(email).isEqualTo(UserFixture.email);
    }

    @Test
    @DisplayName("예외 테스트 : 아이디 찾기 시 일치하는 정보가 없을 때")
    public void findEmailNotMatchRequestTest(){

        assertThatThrownBy(() -> userService.findUserEmail(UserFixture.name, UserFixture.nickname))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_MATCH_EMAIL);

    }

    @Test
    @DisplayName("예외 테스트 : 비밀번호 찾기 시 일치하는 정보가 없을 때")
    public void findPasswordNotMatchRequestTest(){

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> userService.findUserPassword(UserFixture.email, UserFixture.name, UserFixture.nickname))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_USER);

    }

}
