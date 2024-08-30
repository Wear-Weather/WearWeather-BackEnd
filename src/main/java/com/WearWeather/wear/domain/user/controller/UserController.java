package com.WearWeather.wear.domain.user.controller;

import com.WearWeather.wear.domain.user.dto.request.FindUserEmailRequest;
import com.WearWeather.wear.domain.user.dto.request.FindUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserInfoRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.FindUserEmailResponse;
import com.WearWeather.wear.domain.user.dto.response.NicknameDuplicateCheckResponse;
import com.WearWeather.wear.domain.user.dto.response.UserIdForPasswordUpdateResponse;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseCommonDTO> signup(@Valid @RequestBody RegisterUserRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_USER));
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<NicknameDuplicateCheckResponse> checkDuplicateNickname(@PathVariable("nickname") String nickname) {

        userService.checkDuplicatedUserNickName(nickname);
        return ResponseEntity.ok(new NicknameDuplicateCheckResponse(true, ResponseMessage.NICKNAME_AVAILABLE));
    }

    @PostMapping("/email")
    public ResponseEntity<FindUserEmailResponse> findUserEmail(@Valid @RequestBody FindUserEmailRequest request) {

        String email = userService.findUserEmail(request.getName(), request.getNickname());
        return ResponseEntity.ok(new FindUserEmailResponse(email));
    }

    @PostMapping("/password")
    public ResponseEntity<UserIdForPasswordUpdateResponse> findUserPassword(@Valid @RequestBody FindUserPasswordRequest request) {

        return ResponseEntity.ok().body(userService.findUserPassword(request.getEmail(), request.getName(), request.getNickname()));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseCommonDTO> modifyPassword(@LoggedInUser Long userId, @Valid @RequestBody ModifyUserPasswordRequest request) {

        userService.modifyPassword(userId, request.getPassword());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.MODIFY_PASSWORD));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getUserInfo(@LoggedInUser Long userId) {

        UserInfoResponse userInfoResponse = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<ResponseCommonDTO> modifyUserInfo(@LoggedInUser Long userId, @Valid @RequestBody ModifyUserInfoRequest request) {

        userService.modifyUserInfo(
            userId, request.getPassword(), request.getNickname());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.MODIFY_USERINFO));
    }

}
