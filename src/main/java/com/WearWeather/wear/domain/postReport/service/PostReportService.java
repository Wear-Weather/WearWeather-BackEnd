package com.WearWeather.wear.domain.postReport.service;

import com.WearWeather.wear.domain.post.service.PostValidationService;
import com.WearWeather.wear.domain.postReport.entity.PostReport;
import com.WearWeather.wear.domain.postReport.repository.PostReportRepository;
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
public class PostReportService {

    private final PostValidationService postValidationService;
    private final UserService userService;
    private final PostReportRepository postReportRepository;

    @Transactional
    public void reportPost(String userEmail, Long postId, String reason) {
        User user = userService.getUserByEmail(userEmail);

        postValidationService.validatePostExists(postId);

        if (postReportRepository.existsByUserIdAndPostId(user.getUserId(), postId)) {
            throw new CustomException(ErrorCode.REPORT_POST_ALREADY_EXIST);
        }

        PostReport postReport = PostReport.builder()
            .userId(user.getUserId())
            .postId(postId)
            .reason(reason)
            .build();
        postReportRepository.save(postReport);
    }
}
