package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.entity.Like;
import com.WearWeather.wear.domain.post.repository.LikeRepository;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
    public void addLike(Long postId, String userEmail) {

        validatePostExists(postId);
        User user = getUserByEmail(userEmail);
        checkIfAlreadyLiked(postId, user.getUserId());

        Like like = Like.builder()
                        .userId(user.getUserId())
                        .postId(postId)
                        .build();

        likeRepository.save(like);

        postService.incrementLikeCount(postId);
    }

    @Transactional
    public void removeLike(Long postId, String userEmail) {

        validatePostExists(postId);
        User user = getUserByEmail(userEmail);

        Like like = findLike(postId, user.getUserId());
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

}
