package com.WearWeather.wear.domain.user.service;

import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserIdForPasswordUpdateResponse;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest) {

        String encodePassword = passwordEncoder.encode(registerUserRequest.getPassword());
        User user = registerUserRequest.toEntity(encodePassword);

        if (registerUserRequest.isSocial()) {
            user.isSocialLogin();
        }

        if (!registerUserRequest.isSocial()) {
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

    public String findUserEmail(String name, String nickname) {
        Optional<User> user = userRepository.findByNameAndNickname(name, nickname);

        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_MATCH_EMAIL);
        }

        return user.get().getEmail();
    }

    public UserIdForPasswordUpdateResponse findUserPassword(String email, String name, String nickname) {

        User user = userRepository.findByEmailAndNameAndNickname(email, name, nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        return UserIdForPasswordUpdateResponse.of(user.getUserId());

    }

    @Transactional
    public void modifyPassword(String userEmail, String password) {

        User user = getUserByEmail(userEmail);

        try {
            user.updatePassword(passwordEncoder.encode(password), user.isSocial());
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.FAIL_UPDATE_PASSWORD);
        }
    }

    public UserInfoResponse getUserInfo(String userEmail) {

        User user = getUserByEmail(userEmail);

        return UserInfoResponse.of(user);

    }

    @Transactional
    public void modifyUserInfo(String userEmail, String password, String nickname) {

        User user = getUserByEmail(userEmail);

        try {
            user.updateUserInfo(passwordEncoder.encode(password), nickname, user.isSocial());
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.FAIL_UPDATE_USER_INFO);
        }
    }

    public User getUserByEmail(String userEmail){

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_EMAIL));

    }

    public String getNicknameById(Long userId){
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER))
                .getNickname();
    }
}
