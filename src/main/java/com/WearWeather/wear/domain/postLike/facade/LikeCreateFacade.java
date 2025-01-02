package com.WearWeather.wear.domain.postLike.facade;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeCreateFacade {

    private final PostService postService;
    private final LikeService likeService;

    @Transactional
    public TotalLikedCountAfterLike addLike(Long userId, Long postId) {
        validatePostExists(postId);
        likeService.save(userId, postId);
        int likedCount = postService.incrementLikeCount(postId);

        return TotalLikedCountAfterLike.of(likedCount);
    }

    public void validatePostExists(Long postId) {
        postService.validatePostExists(postId);
    }

}
