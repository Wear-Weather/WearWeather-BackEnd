package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.user.dto.RequestRegisterUserDTO;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static com.WearWeather.wear.global.exception.ErrorCode.NICKNAME_ALREADY_EXIST;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final MailService mailService;

    @Transactional
    public void registerUser(RequestRegisterUserDTO requestRegisterUserDTO){

        User user = requestRegisterUserDTO.toEntity();

        if(requestRegisterUserDTO.isSocial()){
            user.isSocialLogin();
        }

        if(!requestRegisterUserDTO.isSocial()){
            user.isRegularLogin();
        }

        userRepository.save(user);
    }

    public void checkDuplicateUserNickname(String nickname){

        Optional<User> existsByNickname = userRepository.findByNickname(nickname);

        if(existsByNickname.isPresent()){
            throw new CustomException(NICKNAME_ALREADY_EXIST);
        }
    }

    public void verifyEmail(String email){

        checkDuplicatedUserEmail(email);

        String title = "[WearWeather] 인증 번호 발송";
        String authCode =
                "WearWeather 회원가입을 위한 " +
                "이메일 인증 번호는 " + createCode() + "입니다." ;

        mailService.sendEmail(email, title, authCode);

    }

    private void checkDuplicatedUserEmail(String email) {
        Optional<User> existsByEmail = userRepository.findByEmail(email);

        if (existsByEmail.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
    }

    private String createCode() {
        int length = 6;

        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

}
