package com.WearWeather.wear.user.controller;

import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.user.dto.RequestVerifyCodeDTO;
import com.WearWeather.wear.user.dto.RequestVerifyEmailDTO;
import com.WearWeather.wear.user.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/email")
@Validated
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;

    @PostMapping("/send-verification")
    public ResponseEntity<ResponseCommonDTO> verifyEmail(@Validated @RequestBody RequestVerifyEmailDTO verifyEmailDTO){
        mailService.verifyEmail(verifyEmailDTO.getEmail());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SEND_EMAIL));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResponseCommonDTO> verifyEmail(@Validated @RequestBody RequestVerifyCodeDTO verifyCodeDTO){
        mailService.checkEmailAuthCode(verifyCodeDTO.getEmail(), verifyCodeDTO.getCode());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_EMAIL_VERIFICATION));
    }
}
