package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest){

        User user = registerUserRequest.toEntity();

        if(registerUserRequest.isSocial()){
            user.isSocialLogin();
        }

        if(!registerUserRequest.isSocial()){
            user.isRegularLogin();
        }

        userRepository.save(user);
    }



    public void checkDuplicatedUserEmail(String email) {

        boolean user = userRepository.existByEmail(email);

        if (user) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
    }


    public void checkDuplicatedUserNickName(String nickname) {

        boolean user = userRepository.existByNickName(nickname);

        if (user) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }


}
