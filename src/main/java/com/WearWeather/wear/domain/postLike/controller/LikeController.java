package com.WearWeather.wear.domain.postLike.controller;

import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterUnlike;
import com.WearWeather.wear.domain.postLike.facade.LikeCreateFacade;
import com.WearWeather.wear.domain.postLike.facade.LikeDeleteFacade;
import com.WearWeather.wear.domain.postLike.facade.LikeReaderFacade;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/likes/posts")
@Validated
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeReaderFacade likeReaderFacade;
    private final LikeCreateFacade likeCreateFacade;
    private final LikeDeleteFacade likeDeleteFacade;

    @PostMapping("/{postId}")
    public ResponseEntity<TotalLikedCountAfterLike> addLike(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeCreateFacade.addLike(userId, postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<TotalLikedCountAfterUnlike> removeLike(@LoggedInUser Long userId, @PathVariable("postId") Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeDeleteFacade.removeLike(userId, postId));
    }

    @GetMapping
    public ResponseEntity<LikedPostsByMeResponse> getLikedPostsByMe(@LoggedInUser Long userId,
        @RequestParam("page") int page,
        @RequestParam("size") int size) {
        return ResponseEntity.ok(likeReaderFacade.getLikedPostsByMe(userId, page, size));
    }

}
