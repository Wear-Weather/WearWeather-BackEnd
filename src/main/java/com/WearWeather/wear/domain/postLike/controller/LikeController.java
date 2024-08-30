package com.WearWeather.wear.domain.postLike.controller;

import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/likes/posts") //TODO : API 명세서 수정하기
@Validated
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> addLike(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        likeService.addLike(userId, postId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_LIKE));
    }

    @DeleteMapping
    public ResponseEntity<ResponseCommonDTO> removeLike(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        likeService.removeLike(postId, userId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_LIKE_CANCEL));
    }

    @GetMapping
    public ResponseEntity<LikedPostsByMeResponse> getLikedPostsByMe(@AuthenticationPrincipal UserDetails userDetail,
                                                                    @RequestParam("page") int page,
                                                                    @RequestParam("size") int size) {
        return ResponseEntity.ok(likeService.getLikedPostsByMe(userDetail.getUsername(), page, size));
    }

}
