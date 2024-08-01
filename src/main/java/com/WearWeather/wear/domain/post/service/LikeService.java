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

        postService.validatePostExists(postId);

        User user = userService.getUserByEmail(userEmail);

        Like like = Like.builder()
                .userId(user.getUserId())
                .postId(postId)
                .build();

        likeRepository.save(like);
    }
}
