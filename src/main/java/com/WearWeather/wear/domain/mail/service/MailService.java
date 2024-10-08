package com.WearWeather.wear.domain.mail.service;

import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.config.RedisConfig;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailService {

    private final JavaMailSender emailSender;
    private final RedisConfig redisConfig;

    private final UserService userService;

    public void verifyEmail(String email) {

        userService.checkDuplicatedUserEmail(email);

        sendEmail(email);

    }

    public void checkEmailAuthCode(String email, String authCode) {
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String code = valOperations.get(email);

        if (!authCode.equals(code)) {
            throw new CustomException(ErrorCode.FAIL_EMAIL_VERIFICATION);
        }

    }

    public String sendEmail(String toEmail) {

        String authCode = createCode();

        String emailFormTitle = getEmailFormTitle();
        String emailFormContent = getEmailFormContent(authCode);

        SimpleMailMessage emailForm = createEmailForm(toEmail, emailFormTitle, emailFormContent);

        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }

        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        valOperations.set(toEmail, authCode, 180, TimeUnit.SECONDS);

        return authCode;
    }

    private SimpleMailMessage createEmailForm(String toEmail, String emailFormTitle, String emailFormContent) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(emailFormTitle);
        message.setText(emailFormContent);

        return message;
    }

    public String getEmailFormTitle() {

        return "[Look At The Weather] 인증 번호 발송";

    }

    public String getEmailFormContent(String authCode) {

        return
            "Look At The Weather 회원가입을 위한 " +
                "이메일 인증 번호는 " + authCode + "입니다.";

    }

    private String createCode() {
        int length = 6;
        int intRange = 10;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(intRange));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

}
