package com.WearWeather.wear.domain.user.service;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.repository.KakaoUserRepository;
import com.WearWeather.wear.domain.oauth.service.RequestOAuthInfoService;
import com.WearWeather.wear.domain.oauth.service.RequestOAuthUnlinkService;
import com.WearWeather.wear.domain.user.dto.request.DeleteUserRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserIdForPasswordUpdateResponse;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.entity.UserDelete;
import com.WearWeather.wear.domain.user.repository.DeleteReasonRepository;
import com.WearWeather.wear.domain.user.repository.UserDeleteRepository;
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
    private final UserDeleteRepository userDeleteRepository;
    private final DeleteReasonRepository deleteReasonRepository;
    private final KakaoUserRepository kakaoUserRepository;
    private final RequestOAuthUnlinkService requestOAuthUnlinkService;

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

        boolean user = userRepository.existsByEmailAndIsDeleteFalse(email);

        if (user) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
    }


    public void checkDuplicatedUserNickName(String nickname) {

        boolean user = userRepository.existsByNicknameAndIsDeleteFalse(nickname);

        if (user) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }

    public String findUserEmail(String name, String nickname) {
        Optional<User> user = userRepository.findByNameAndNicknameAndIsDeleteFalse(name, nickname);

        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_MATCH_EMAIL);
        }

        return user.get().getEmail();
    }

    public UserIdForPasswordUpdateResponse findUserPassword(String email, String name, String nickname) {

        User user = userRepository.findByEmailAndNameAndNicknameAndIsDeleteFalse(email, name, nickname)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        return UserIdForPasswordUpdateResponse.of(user.getUserId());

    }

    @Transactional
    public void modifyPassword(ModifyUserPasswordRequest request) {

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        try {
            user.updatePassword(passwordEncoder.encode(request.getPassword()), user.isSocial());
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.FAIL_UPDATE_PASSWORD);
        }
    }

    public UserInfoResponse getUserInfo(Long userId) {

        User user = getUserById(userId);

        return UserInfoResponse.of(user);

    }

    @Transactional
    public void modifyUserInfo(Long userId, String password, String nickname) {

        User user = getUserById(userId);

        try {
            user.updateUserInfo(passwordEncoder.encode(password), nickname, user.isSocial());
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.FAIL_UPDATE_USER_INFO);
        }
    }

    public User getUserByEmail(String userEmail) {

        return userRepository.findByEmailAndIsDeleteFalse(userEmail)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_EMAIL));

    }

    public User getUserById(Long userId) {

        return userRepository.findByUserIdAndIsDeleteFalse(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_EMAIL));

    }

    public String getNicknameById(Long userId) {
        return userRepository.findNicknameByUserIdAndIsDeleteFalse(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER))
            .getNickname();
    }

    @Transactional
    public void deleteUser(Long userId, DeleteUserRequest request) {
        User user = getUserById(userId);

        if(!existsDeleteReason(request.deleteReasonId())){
            throw new CustomException(ErrorCode.INVALID_DELETE_REASON);
        }

        if(user.isDelete()){
            throw new CustomException(ErrorCode.ALREADY_DELETE_USER);
        }

        user.updateIsDelete();

        if (!userDeleteRepository.existsByUserId(userId)) {
            UserDelete userDelete = request.toEntity(userId);
            userDeleteRepository.save(userDelete);
        }

        if(user.isSocial()){
            Long kakaoUserId = kakaoUserRepository.findKakaoUserIdByUserId(userId);
            if (kakaoUserId == null) {
                throw new CustomException(ErrorCode.KAKAO_USER_NOT_FOUND);
            }

            requestOAuthUnlinkService.request(kakaoUserId);
            kakaoUserRepository.deleteByKakaoUserId(kakaoUserId);
        }
    }

    public boolean existsDeleteReason(Long reasonId){
        return deleteReasonRepository.existsById(reasonId);
    }
}
