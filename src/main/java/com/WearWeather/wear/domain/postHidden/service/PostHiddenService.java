package com.WearWeather.wear.domain.postHidden.service;

import com.WearWeather.wear.domain.post.service.PostValidationService;
import com.WearWeather.wear.domain.postHidden.entity.PostHidden;
import com.WearWeather.wear.domain.postHidden.repository.PostHiddenRepository;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostHiddenService {

    private final PostHiddenRepository postHiddenRepository;
    private final UserService userService;
    private final PostValidationService postValidationService;

    @Transactional
    public void hidePost(Long userId, Long postId) {
        postValidationService.validatePostExists(postId);

        if (postHiddenRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new CustomException(ErrorCode.HIDDEN_POST_ALREADY_EXIST);
        }

        PostHidden postHidden = PostHidden.builder()
            .userId(userId)
            .postId(postId)
            .build();
        postHiddenRepository.save(postHidden);
    }
}
