package com.WearWeather.wear.domain.post.facade;

import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostDeleteFacade {

    private final PostService postService;
    private final PostTagService postTagService;
    private final PostImageService postImageService;
    private final PostHiddenService postHiddenService;
    private final PostReportService postReportService;
    private final LikeService likeService;

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = validateUserPermission(userId, postId);
        deleteRelatedDataByPostId(postId);
        postService.delete(post);
    }

    public Post validateUserPermission(Long userId, Long postId) {
        Post post = postService.getPost(postId);
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return post;
    }

    private void deleteRelatedDataByPostId(Long postId) {
        postImageService.deleteImagesByPostId(postId);
        postTagService.deleteTagsByPostId(postId);
        postHiddenService.deleteHiddenByPostId(postId);
        postReportService.deleteReportByPostId(postId);
        likeService.deleteLikeByPostId(postId);
    }

}
