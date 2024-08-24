package com.WearWeather.wear.domain.postReport;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postReport.entity.PostReport;
import com.WearWeather.wear.domain.postReport.repository.PostReportRepository;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.domain.user.entity.User;
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
    private PostService postService;

    @Test
    @DisplayName("정상 테스트: 게시글 신고 성공")
    public void successfulReportPost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        String reason = "Inappropriate content";
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(user.getUserId()).thenReturn(1L);
        doNothing().when(postService).validatePostExists(postId);
        when(postReportRepository.existsByUserIdAndPostId(1L, postId)).thenReturn(false);

        // when
        postReportService.reportPost(userEmail, postId, reason);

        // then
        verify(postReportRepository, times(1)).save(any(PostReport.class));
    }

    @Test
    @DisplayName("예외 테스트: 이미 신고한 게시글 신고 시도")
    public void reportPostWithAlreadyReportedPost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        String reason = "Inappropriate content";
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(user.getUserId()).thenReturn(1L);
        doNothing().when(postService).validatePostExists(postId);
        when(postReportRepository.existsByUserIdAndPostId(1L, postId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> postReportService.reportPost(userEmail, postId, reason))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPORT_POST_ALREADY_EXIST);
    }

    @Test
    @DisplayName("예외 테스트: 존재하지 않는 게시글 신고 시도")
    public void reportPostWithNonexistentPost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        String reason = "Inappropriate content";
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        doThrow(new CustomException(ErrorCode.NOT_EXIST_POST)).when(postService).validatePostExists(postId);

        // when & then
        assertThatThrownBy(() -> postReportService.reportPost(userEmail, postId, reason))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_POST);
    }
}
