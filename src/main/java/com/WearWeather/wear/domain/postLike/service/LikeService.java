package com.WearWeather.wear.domain.postLike.service;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterUnlike;
import com.WearWeather.wear.domain.postLike.entity.Like;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public Like findLike(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_LIKED_POST));
    }

    public void deleteLikeByPostId(Long postId) {
        likeRepository.deleteByPostId(postId);
    }

    public List<Long> getMostLikedPostIdForDay(List<Long> invisiblePostIds) {
        return likeRepository.findMostLikedPostIdForDay(invisiblePostIds);
    }

    public boolean checkLikeByPostAndUser(Long postId, Long userId){
        if (userId == null) {
            return false;
        }
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public Page<Long> getByUserIdNotInHiddenPosts(Long userId, Pageable pageable, List<Long> invisiblePostIdsList) {
        return likeRepository.findByUserIdNotInHiddenPosts(userId, pageable, invisiblePostIdsList);
    }

    public void checkIfAlreadyLiked(Long postId, Long userId) {
        boolean checkLike = likeRepository.existsByPostIdAndUserId(postId, userId);
        if (checkLike) {
            throw new CustomException(ErrorCode.ALREADY_LIKED_POST);
        }
    }

    public void save(Long userId, Long postId) {
        checkIfAlreadyLiked(postId, userId);

        Like like = Like.builder()
          .userId(userId)
          .postId(postId)
          .build();

        likeRepository.save(like);
    }

    public void delete(Long userId, Long postId) {
        Like like = findLike(postId, userId);
        likeRepository.delete(like);
    }
}
