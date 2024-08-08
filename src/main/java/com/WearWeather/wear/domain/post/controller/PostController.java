package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.GetPostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.GetPostListResponse;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/top-liked")
    public ResponseEntity<GetPostListResponse> getTopLikedPosts(@AuthenticationPrincipal UserDetails userDetails) {
        List<GetPostDetailResponse> response = postService.getTopLikedPosts(userDetails.getUsername());
        return ResponseEntity.ok(new GetPostListResponse(response));
    }
}
