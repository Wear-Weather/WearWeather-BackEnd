package com.WearWeather.wear.user.controller;

import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.user.dto.RequestEmailCheckDTO;
import com.WearWeather.wear.user.dto.RequestRegisterUserDTO;
import com.WearWeather.wear.user.dto.ResponseDuplicateCheckDTO;
import com.WearWeather.wear.user.service.UserService;
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
    public ResponseEntity<ResponseCommonDTO> signup(@Validated @RequestBody RequestRegisterUserDTO registerUserDTO){

        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(new ResponseCommonDTO(true, "User registered successfully."));
    }

    @GetMapping("/nickname-check/{nickname}")
    public ResponseEntity<ResponseDuplicateCheckDTO> checkDuplicateNickname(@PathVariable("nickname") String nickname){

        userService.checkDuplicateNickname(nickname);
        return ResponseEntity.ok(new ResponseDuplicateCheckDTO(true, ResponseMessage.NICKNAME_AVAILABLE));
    }

}
