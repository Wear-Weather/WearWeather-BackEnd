package com.WearWeather.wear.domain.mail.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.config.RedisConfig;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("MemberService 테스트")
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Autowired
    UserService userService;

    @Autowired
    RedisConfig redisConfig;

    @Test
    @DisplayName("예외 테스트 : 이미 등록된 이메일로 인증할 때")
    public void verifyEmailTest() {

        RegisterUserRequest request = UserFixture.createRegisterUserRequest();
        userService.registerUser(request);

        assertThatThrownBy(() -> mailService.verifyEmail(UserFixture.email))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXIST);

    }

    @Test
    @DisplayName("정상 테스트 : 인증 메일이 전송될 때")
    public void sendEmailTest() {

        String authCode = mailService.sendEmail(UserFixture.email);

        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String savedAuthCode = valOperations.get(UserFixture.email);

        assertEquals(authCode, savedAuthCode);
    }

    @Test
    @DisplayName("정상 테스트 : 인증 코드와 입력한 인증 코드가 일치할 때")
    public void matchAuthCodeTest() {

        String authCode = mailService.sendEmail(UserFixture.email);

        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String savedAuthCode = valOperations.get(UserFixture.email);

        assertNotNull(savedAuthCode);
        assertEquals(authCode, savedAuthCode);
    }

    @Test
    @DisplayName("예외 테스트 : 이메일 인증코드와 입력된 인증코드가 일치하지 않을 때")
    public void notMatchAuthCodeTest() {

        String wrongAuthCode = "123456";

        mailService.sendEmail(UserFixture.email);

        assertThatThrownBy(() -> mailService.checkEmailAuthCode(UserFixture.email, wrongAuthCode))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAIL_EMAIL_VERIFICATION);

    }

}
