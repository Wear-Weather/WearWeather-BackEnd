package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.user.dto.RequestRegisterUserDTO;
import com.WearWeather.wear.user.dto.ResponseDuplicateCheckDTO;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.WearWeather.wear.global.exception.ErrorCode.NICKNAME_ALREADY_EXIST;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

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

    public void checkDuplicateNickname(String nickname){

        boolean nicknameExist = userRepository.findByNickname(nickname).isPresent();

        if(nicknameExist){
            throw new CustomException(NICKNAME_ALREADY_EXIST);
        }
    }

}
