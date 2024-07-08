package com.WearWeather.wear.user.controller;

import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.user.dto.RequestRegisterUserDTO;
import com.WearWeather.wear.user.dto.RequestVerifyEmailDTO;
import com.WearWeather.wear.user.dto.ResponseDuplicateCheckDTO;
import com.WearWeather.wear.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@Validated
@RestController
@RequiredArgsConstructor
@Tag( name = "AddressController", description = "[사용자] 배송지 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<ResponseCommonDTO> signup(@Valid @RequestBody RequestRegisterUserDTO registerUserDTO){

        userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(new ResponseCommonDTO(true, "User registered successfully."));
    }

    @GetMapping("/users/nickname-check/{nickname}")
    public ResponseEntity<ResponseDuplicateCheckDTO> checkDuplicateNickname(@PathVariable("nickname") String nickname){

        userService.checkDuplicateNickname(nickname);
        return ResponseEntity.ok(new ResponseDuplicateCheckDTO(true, ResponseMessage.NICKNAME_AVAILABLE));
    }

    @PostMapping("/email/send-verification")
    public ResponseEntity<ResponseCommonDTO> verifyEmail(@Validated @RequestBody RequestVerifyEmailDTO verifyEmailDTO){
        userService.verifyEmail(verifyEmailDTO.getEmail());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SEND_EMAIL));
    }
}
