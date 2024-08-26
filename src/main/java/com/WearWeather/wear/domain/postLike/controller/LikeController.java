package com.WearWeather.wear.domain.postLike.controller;

import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/likes/posts") //TODO : API 명세서 수정하기
@Validated
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> addLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetail) {
        likeService.addLike(postId, userDetail.getUsername());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_LIKE));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> removeLike(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetail) {
        likeService.removeLike(postId, userDetail.getUsername());
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_LIKE_CANCEL));
    }
}
