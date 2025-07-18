package com.WearWeather.wear.domain.postReport.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostValidationService;
import com.WearWeather.wear.domain.postReport.entity.PostReport;
import com.WearWeather.wear.domain.postReport.repository.PostReportRepository;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostReportServiceTest {

    @InjectMocks
    private PostReportService postReportService;

    @Mock
    private PostReportRepository postReportRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostValidationService postValidationService;

    @Test
    @DisplayName("정상 테스트: 게시글 신고 성공")
    public void successfulReportPost() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        String reason = "Inappropriate content";

        doNothing().when(postValidationService).validatePostExists(postId);
        when(postReportRepository.existsByUserIdAndPostId(1L, postId)).thenReturn(false);

        // when
        postReportService.reportPost(userId, postId, reason);

        // then
        verify(postReportRepository, times(1)).save(any(PostReport.class));
    }

    @Test
    @DisplayName("예외 테스트: 이미 신고한 게시글 신고 시도")
    public void reportPostWithAlreadyReportedPost() {
        // given
        String userEmail = "test@example.com";
        Long userId = 1L;
        Long postId = 1L;
        String reason = "Inappropriate content";

        doNothing().when(postValidationService).validatePostExists(postId);
        when(postReportRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> postReportService.reportPost(userId, postId, reason))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPORT_POST_ALREADY_EXIST);
    }

    @Test
    @DisplayName("예외 테스트: 존재하지 않는 게시글 신고 시도")
    public void reportPostWithNonexistentPost() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        String reason = "Inappropriate content";

        doThrow(new CustomException(ErrorCode.NOT_EXIST_POST)).when(postValidationService).validatePostExists(postId);

        // when & then
        assertThatThrownBy(() -> postReportService.reportPost(userId, postId, reason))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_POST);
    }
}
