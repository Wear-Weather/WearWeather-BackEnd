package com.WearWeather.wear.domain.postReport.service;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postReport.entity.PostReport;
import com.WearWeather.wear.domain.postReport.repository.PostReportRepository;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReportService {

    private final PostService postService;
    private final UserService userService;
    private final PostReportRepository postReportRepository;

    @Transactional
    public void reportPost(Long userId, Long postId, String reason) {
        postService.validatePostExists(postId);

        if (postReportRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new CustomException(ErrorCode.REPORT_POST_ALREADY_EXIST);
        }

        PostReport postReport = PostReport.builder()
            .userId(userId)
            .postId(postId)
            .reason(reason)
            .build();
        postReportRepository.save(postReport);
    }
}
