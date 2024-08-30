package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByFiltersResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostsResponse;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.response.*;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> createPost(@LoggedInUser Long userId, @RequestBody @Valid PostCreateRequest request) {
        Long postId = postService.createPost(userId, request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST));
    }

    @GetMapping("/top-liked")
    public ResponseEntity<TopLikedPostsResponse> getTopLikedPosts(@LoggedInUser Long userId) {
        List<TopLikedPostResponse> response = postService.getTopLikedPosts(userId);
        return ResponseEntity.ok(new TopLikedPostsResponse(response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> updatePost(@LoggedInUser Long userId, @PathVariable Long postId, @RequestBody @Valid PostUpdateRequest request) {
        postService.updatePost(userId, postId, request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_UPDATE_POST));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> deletePost(@LoggedInUser Long userId, @PathVariable Long postId) {
        postService.deletePost(userId, postId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_DELETE_POST));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(userId, postId));
    }

    @GetMapping
    public ResponseEntity<PostsByLocationResponse> getPostsByLocation(@LoggedInUser Long userId,
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("city") String city,
        @RequestParam("district") String district,
        @RequestParam("sort") SortType sort) {
        return ResponseEntity.ok(postService.getPostsByLocation(userId, page, size, city, district, sort));
    }

    @PostMapping("/search")
    public ResponseEntity<PostsByFiltersResponse> searchPostsWithFilters(@LoggedInUser Long userId, @Valid @RequestBody PostsByFiltersRequest request) {
        return ResponseEntity.ok(postService.searchPostsWithFilters(userId, request));
    }


    @GetMapping("/me")
    public ResponseEntity<PostsByMeResponse> getPostsByMe(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size) {
        return ResponseEntity.ok(postService.getPostsByMe(userDetails.getUsername(), page, size));
    }
}
