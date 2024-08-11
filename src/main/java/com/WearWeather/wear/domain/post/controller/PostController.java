package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostsByLocationRequest;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostsResponse;
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
    public ResponseEntity<TopLikedPostsResponse> getTopLikedPosts(@AuthenticationPrincipal UserDetails userDetails) {
        List<TopLikedPostDetailResponse> response = postService.getTopLikedPosts(userDetails.getUsername());
        return ResponseEntity.ok(new TopLikedPostsResponse(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(userDetails.getUsername(), postId));
    }

    @GetMapping
    public ResponseEntity<PostsByLocationResponse> getPostsByLocation(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PostsByLocationRequest request) {
        return ResponseEntity.ok(postService.getPostsByLocation(userDetails.getUsername(), request));
    }
}
