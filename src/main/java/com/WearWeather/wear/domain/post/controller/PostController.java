package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostsResponse;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.response.*;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/top-liked")
    public ResponseEntity<TopLikedPostsResponse> getTopLikedPosts(@AuthenticationPrincipal UserDetails userDetails) {
        List<TopLikedPostResponse> response = postService.getTopLikedPosts(userDetails.getUsername());
        return ResponseEntity.ok(new TopLikedPostsResponse(response));
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

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(userDetails.getUsername(), postId));
    }

    @GetMapping
    public ResponseEntity<PostsByLocationResponse> getPostsByLocation(@AuthenticationPrincipal UserDetails userDetails,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size,
                                                                      @RequestParam("city") String city,
                                                                      @RequestParam("district") String district,
                                                                      @RequestParam("sort") SortType sort) {
        return ResponseEntity.ok(postService.getPostsByLocation(userDetails.getUsername(), page, size, city, district, sort));
    }

    @PostMapping("/search")
    public ResponseEntity<PostsByFiltersResponse> searchPostsWithFilters(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody PostsByFiltersRequest request) {
        return ResponseEntity.ok(postService.searchPostsWithFilters(userDetails.getUsername(), request));
    }

}
