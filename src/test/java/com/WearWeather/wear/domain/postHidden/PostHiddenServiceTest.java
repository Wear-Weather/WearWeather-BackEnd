package com.WearWeather.wear.domain.postHidden;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postHidden.entity.PostHidden;
import com.WearWeather.wear.domain.postHidden.repository.PostHiddenRepository;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
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
public class PostHiddenServiceTest {

    @InjectMocks
    private PostHiddenService postHiddenService;

    @Mock
    private PostHiddenRepository postHiddenRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Test
    @DisplayName("정상 테스트: 게시글 숨기기 성공")
    public void successfulHidePost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(user.getUserId()).thenReturn(1L);
        doNothing().when(postService).validatePostExists(postId);
        when(postHiddenRepository.existsByUserIdAndPostId(1L, postId)).thenReturn(false);

        // when
        postHiddenService.hidePost(userEmail, postId);

        // then
        verify(postHiddenRepository, times(1)).save(any(PostHidden.class));
    }

    @Test
    @DisplayName("예외 테스트: 이미 숨긴 게시글 숨기기 시도")
    public void hidePostWithAlreadyHiddenPost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(user.getUserId()).thenReturn(1L);
        doNothing().when(postService).validatePostExists(postId);
        when(postHiddenRepository.existsByUserIdAndPostId(1L, postId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> postHiddenService.hidePost(userEmail, postId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.HIDDEN_POST_ALREADY_EXIST);
    }

    @Test
    @DisplayName("예외 테스트: 존재하지 않는 게시글 숨기기 시도")
    public void hidePostWithNonexistentPost() {
        // given
        String userEmail = "test@example.com";
        Long postId = 1L;
        User user = mock(User.class);

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        doThrow(new CustomException(ErrorCode.NOT_EXIST_POST)).when(postService).validatePostExists(postId);

        // when & then
        assertThatThrownBy(() -> postHiddenService.hidePost(userEmail, postId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EXIST_POST);
    }
}