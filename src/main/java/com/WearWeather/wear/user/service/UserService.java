package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.config.RedisConfig;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.user.dto.RequestRegisterUserDTO;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final MailService mailService;
    private final RedisConfig redisConfig;

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

    public void verifyEmail(String email){

        checkDuplicatedUserEmail(email);

        mailService.sendEmail(email);

    }

    private void checkDuplicatedUserEmail(String email) {

        Optional<User> existByEmail = userRepository.findByEmail(email);

        if (existByEmail.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
    }


    public void checkDuplicatedUserNickName(String nickname) {

        Optional<User> existByNickName = userRepository.findByNickname(nickname);

        if (existByNickName.isPresent()) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }

    public void checkEmailAuthCode(String email, String authCode){
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String code = valOperations.get(email);

        if(!authCode.equals(code)){
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }

    }

}
