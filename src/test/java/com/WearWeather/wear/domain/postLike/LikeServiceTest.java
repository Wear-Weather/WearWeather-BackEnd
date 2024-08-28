package com.WearWeather.wear.domain.postLike;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.entity.Like;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    PostService postService;

    @Mock
    UserService userService;

    @Test
    @DisplayName("정상 테스트 : 좋아요 테스트")
    public void addLikePostTest() {

        Long userId = 1L;
        Long postId = 1L;

        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);

        likeService.addLike(postId, userId);

        verify(likeRepository).save(argThat(like ->
            like.getUserId().equals(userId) &&
                like.getPostId().equals(postId)
        ));
    }

    @Test
    @DisplayName("예외 테스트 : 이미 좋아요된 게시글 테스트")
    public void addLikePostWithAlreadyLikedPostTest() {

        Long userId = 1L;
        Long postId = 1L;

        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.addLike(postId, userId));
        assertEquals(ErrorCode.ALREADY_LIKED_POST, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 좋아요 취소 테스트")
    public void removeLikePostTest() {

        Long userId = 1L;
        Long postId = 1L;

        Like like = mock(Like.class);

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        likeService.removeLike(postId, userId);

        verify(likeRepository).delete(like);
        verify(postService).removeLikeCount(postId);
    }

    @Test
    @DisplayName("예외 테스트 : 좋아요 되지않은 게시글 테스트")
    public void findEmailNotMatchRequestTest() {

        User user = mock(User.class);
        Post post = mock(Post.class);

        when(likeRepository.findByPostIdAndUserId(post.getId(), user.getUserId())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
            likeService.removeLike(post.getId(), user.getUserId()));
        assertEquals(ErrorCode.NOT_LIKED_POST, exception.getErrorCode());
    }
}
