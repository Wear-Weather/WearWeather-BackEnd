package com.WearWeather.wear.user.service;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        boolean user = userRepository.existsByEmail(email);

        if (user) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
    }


    public void checkDuplicatedUserNickName(String nickname) {

        boolean user = userRepository.existsByNickname(nickname);

        if (user) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }

    public String findUserEmail(String name, String nickname){
        Optional<User> user = userRepository.findByNameAndNickname(name, nickname);

        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_MATCH_EMAIL);
        }

        return user.get().getEmail();
    }

    public void findUserPassword(String email, String name, String nickname){
        boolean user = userRepository.existsByEmailAndNameAndNickname(email, name, nickname);

        if (user) {
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
    }

}
