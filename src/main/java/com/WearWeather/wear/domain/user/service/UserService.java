package com.WearWeather.wear.domain.user.service;

import com.WearWeather.wear.domain.oauth.infrastructure.kakao.dto.KakaoUserDto;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.entity.KakaoUser;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.service.KakaoUserService;
import com.WearWeather.wear.domain.oauth.service.RequestOAuthUnlinkService;
import com.WearWeather.wear.domain.user.dto.request.DeleteReasonRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserIdForPasswordUpdateResponse;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.enums.DeleteReason;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final RequestOAuthUnlinkService requestOAuthUnlinkService;
    private final KakaoUserService kakaoUserService;
    private final UserDeleteService userDeleteService;
    private final UserRepository userRepository;

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

        User user = getUser(userId);

        return UserInfoResponse.of(user);

    }

    @Transactional
    public void modifyUserInfo(Long userId, String password, String nickname) {

        User user = getUser(userId);

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

    public User getUser(Long userId) {

        return userRepository.findByUserIdAndIsDeleteFalse(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

    }

    public String getNicknameById(Long userId) {
        return userRepository.findNicknameByUserIdAndIsDeleteFalse(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER))
            .getNickname();
    }

    @Transactional
    public void deleteUser(Long userId, String reason) {
        DeleteReason deleteReason = getDeleteReason(reason);
        User user = getUser(userId);
        user.updateIsDelete();

        userDeleteService.save(user, deleteReason);

        if (user.isSocial()) {
            KakaoUser kakaoUser = kakaoUserService.getKakaoUserByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.KAKAO_USER_NOT_FOUND));

            requestOAuthUnlinkService.request(KakaoUserDto.of(kakaoUser));
            kakaoUserService.deleteKakaoUser(kakaoUser);
        }
    }

    private DeleteReason getDeleteReason(String reason) {
        return Arrays.stream(DeleteReason.values())
            .filter(deleteReason -> deleteReason.getDescription().equals(reason))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_DELETE_REASON));
    }
}
