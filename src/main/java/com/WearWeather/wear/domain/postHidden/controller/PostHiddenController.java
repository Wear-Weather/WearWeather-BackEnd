package com.WearWeather.wear.domain.postHidden.controller;


import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts/{postId}/hidden")
@RequiredArgsConstructor
@RestController
public class PostHiddenController {

    private final PostHiddenService postHiddenService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> hidePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postHiddenService.hidePost(userDetails.getUsername(), postId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_HIDDEN));
    }
}
