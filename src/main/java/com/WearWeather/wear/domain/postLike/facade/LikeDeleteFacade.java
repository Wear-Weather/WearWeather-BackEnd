package com.WearWeather.wear.domain.postLike.facade;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterUnlike;
import com.WearWeather.wear.domain.postLike.entity.Like;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeDeleteFacade {

    private final PostService postService;
    private final LikeService likeService;

    @Transactional
    public TotalLikedCountAfterUnlike removeLike(Long userId, Long postId) {
        validatePostExists(postId);
        likeService.delete(userId, postId);
        int likedCount = postService.removeLikeCount(postId);

        return TotalLikedCountAfterUnlike.of(likedCount);
    }

    public void validatePostExists(Long postId) {
        postService.validatePostExists(postId);
    }

}
