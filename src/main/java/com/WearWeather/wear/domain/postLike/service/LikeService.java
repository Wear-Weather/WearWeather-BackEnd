package com.WearWeather.wear.domain.postLike.service;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostByMeResponse;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.entity.Like;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;

    @Transactional
    public void addLike(Long userId, Long postId) {

        validatePostExists(postId);
        checkIfAlreadyLiked(postId, userId);

        Like like = Like.builder()
            .userId(userId)
            .postId(postId)
            .build();

        likeRepository.save(like);

        postService.incrementLikeCount(postId);
    }

    @Transactional
    public void removeLike(Long userId, Long postId) {

        validatePostExists(postId);

        Like like = findLike(postId, userId);
        likeRepository.delete(like);

        postService.removeLikeCount(postId);

    }

    public void validatePostExists(Long postId) {
        postService.validatePostExists(postId);
    }

    public User getUserByEmail(String userEmail) {
        return userService.getUserByEmail(userEmail);
    }

    public void checkIfAlreadyLiked(Long postId, Long userId) {
        boolean checkLike = likeRepository.existsByPostIdAndUserId(postId, userId);

        if (checkLike) {
            throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
        }
    }

    public Like findLike(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_LIKED_POST));
    }

    public LikedPostsByMeResponse getLikedPostsByMe(Long userId, int page, int size) {

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        List<Long> likedPostIds = likeRepository.findByUserId(userId, pageable);

        List<LikedPostByMeResponse> likedPosts = postService.getLikedPostsByMe(userId, likedPostIds);

        return LikedPostsByMeResponse.of(likedPosts);
    }
}
