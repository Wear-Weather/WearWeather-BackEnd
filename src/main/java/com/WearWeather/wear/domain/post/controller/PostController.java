package com.WearWeather.wear.domain.post.controller;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostCreateResponse;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByFiltersResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostsResponse;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.facade.PostCreateFacade;
import com.WearWeather.wear.domain.post.facade.PostDeleteFacade;
import com.WearWeather.wear.domain.post.facade.PostReaderFacade;
import com.WearWeather.wear.domain.post.facade.PostUpdateFacade;
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

    private final PostCreateFacade postCreateFacade;
    private final PostUpdateFacade postUpdateFacade;
    private final PostDeleteFacade postDeleteFacade;
    private final PostReaderFacade postReaderFacade;

    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(@LoggedInUser Long userId, @RequestBody @Valid PostCreateRequest request) {
        Long postId = postCreateFacade.createPost(userId, request);
        return ResponseEntity.ok(new PostCreateResponse(postId));
    }

    @GetMapping("/top-liked")
    public ResponseEntity<TopLikedPostsResponse> getTopLikedPosts(@LoggedInUser Long userId) {
        List<TopLikedPostResponse> response = postReaderFacade.getTopLikedPosts(userId);
        return ResponseEntity.ok(new TopLikedPostsResponse(response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> updatePost(@LoggedInUser Long userId, @PathVariable Long postId, @RequestBody @Valid PostUpdateRequest request) {
        postUpdateFacade.updatePost(userId, postId, request);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_UPDATE_POST));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseCommonDTO> deletePost(@LoggedInUser Long userId, @PathVariable Long postId) {
        postDeleteFacade.deletePost(userId, postId);
        return ResponseEntity.ok(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_DELETE_POST));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postReaderFacade.getPostDetail(userId, postId));
    }

    @GetMapping
    public ResponseEntity<PostsByLocationResponse> getPostsByLocation(@LoggedInUser Long userId,
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("city") String city,
        @RequestParam("district") String district,
        @RequestParam("sort") SortType sort) {
        return ResponseEntity.ok(postReaderFacade.getPostsByLocation(userId, page, size, city, district, sort));
    }

    @PostMapping("/search")
    public ResponseEntity<PostsByFiltersResponse> getPosts(@LoggedInUser Long userId, @Valid @RequestBody PostsByFiltersRequest request) {
        return ResponseEntity.ok(postReaderFacade.getPosts(userId, request));
    }

    @GetMapping("/tmp")
    public ResponseEntity<PostsByTemperatureResponse> getPostsByTemperature(@LoggedInUser Long userId,
        @RequestParam("tmp") int tmp,
        @RequestParam("page") int page,
        @RequestParam("size") int size) {
        return ResponseEntity.ok(postReaderFacade.getPostsByTemperature(userId, tmp, page, size));
    }

    @GetMapping("/me")
    public ResponseEntity<PostsByMeResponse> getPostsByMe(@LoggedInUser Long userId,
      @RequestParam("page") int page,
      @RequestParam("size") int size) {
        return ResponseEntity.ok(postReaderFacade.getPostsByMe(userId, page, size));
    }
}
