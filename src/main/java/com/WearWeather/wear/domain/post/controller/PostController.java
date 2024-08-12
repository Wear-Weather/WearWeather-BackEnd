package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> createPost(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid PostCreateRequest request) {
        Long postId = postService.createPost(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> updatePost(@PathVariable Long postId, @RequestBody @Valid PostUpdateRequest request) {
        postService.updatePost(postId, request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_UPDATE_POST));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_DELETE_POST));
    }
}
