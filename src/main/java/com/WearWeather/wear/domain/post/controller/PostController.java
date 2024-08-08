package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> createPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostCreateRequest request) {
        Long postId = postService.createPost(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(userDetails.getUsername(), postId));
    }
}
