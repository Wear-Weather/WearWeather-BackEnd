package com.WearWeather.wear.user.controller;

import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.user.dto.response.NicknameDuplicateCheckResponse;
import com.WearWeather.wear.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseCommonDTO> signup(@Valid @RequestBody RegisterUserRequest registerUserDTO){

        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(new ResponseCommonDTO(true, "User registered successfully."));
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<NicknameDuplicateCheckResponse> checkDuplicateNickname(@PathVariable("nickname") String nickname){

        userService.checkDuplicatedUserNickName(nickname);
        return ResponseEntity.ok(new NicknameDuplicateCheckResponse(true, ResponseMessage.NICKNAME_AVAILABLE));
    }

}
