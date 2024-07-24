package com.WearWeather.wear.user.controller;

import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.user.dto.request.FindUserEmailRequest;
import com.WearWeather.wear.user.dto.request.FindUserPasswordRequest;
import com.WearWeather.wear.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.user.dto.response.FindUserEmailResponse;
import com.WearWeather.wear.user.dto.response.NicknameDuplicateCheckResponse;
import com.WearWeather.wear.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseCommonDTO> signup(@Valid @RequestBody RegisterUserRequest request){

        userService.registerUser(request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, "User registered successfully."));
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<NicknameDuplicateCheckResponse> checkDuplicateNickname(@PathVariable("nickname") String nickname){

        userService.checkDuplicatedUserNickName(nickname);
        return ResponseEntity.ok(new NicknameDuplicateCheckResponse(true, ResponseMessage.NICKNAME_AVAILABLE));
    }

    @PostMapping("/email")
    public ResponseEntity<FindUserEmailResponse> findUserEmail(@Valid @RequestBody FindUserEmailRequest request){

        String email = userService.findUserEmail(request.getName(), request.getNickname());
        return ResponseEntity.ok(new FindUserEmailResponse(email));
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseCommonDTO> findUserPassword(@Valid @RequestBody FindUserPasswordRequest request){

        userService.findUserPassword(request.getEmail(), request.getName(), request.getNickname());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.EXIST_USER));
    }

}
