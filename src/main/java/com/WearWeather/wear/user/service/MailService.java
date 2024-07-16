package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.config.RedisConfig;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailService {

    private final JavaMailSender emailSender;
    private final RedisConfig redisConfig;

    public void sendEmail(String toEmail) {

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

    }

    private SimpleMailMessage createEmailForm(String toEmail,String emailFormTitle, String emailFormContent) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(emailFormTitle);
        message.setText(emailFormContent);

        return message;
    }

    public String getEmailFormTitle(){

        return  "[WearWeather] 인증 번호 발송";

    }

    public String getEmailFormContent(String authCode){

        return
                "WearWeather 회원가입을 위한 " +
                        "이메일 인증 번호는 " + authCode + "입니다." ;

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
