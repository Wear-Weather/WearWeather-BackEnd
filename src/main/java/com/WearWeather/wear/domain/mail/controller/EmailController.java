package com.WearWeather.wear.domain.mail.controller;

import com.WearWeather.wear.domain.mail.dto.request.VerifyEmailAuthCodeRequest;
import com.WearWeather.wear.domain.mail.dto.request.VerifyEmailRequest;
import com.WearWeather.wear.domain.mail.service.MailService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/email")
@Validated
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;

    @PostMapping("/send-verification")
    public ResponseEntity<ResponseCommonDTO> verifyEmail(@Validated @RequestBody VerifyEmailRequest verifyEmailDTO) {
        mailService.verifyEmail(verifyEmailDTO.getEmail());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SEND_EMAIL));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResponseCommonDTO> verifyEmail(@Validated @RequestBody VerifyEmailAuthCodeRequest verifyCodeDTO) {
        mailService.checkEmailAuthCode(verifyCodeDTO.getEmail(), verifyCodeDTO.getCode());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_EMAIL_VERIFICATION));
    }
}
