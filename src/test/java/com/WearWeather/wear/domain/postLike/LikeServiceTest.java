package com.WearWeather.wear.domain.postLike;

import com.WearWeather.wear.domain.post.entity.Like;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        User user = mock(User.class);
        Post post = mock(Post.class);
        String userEmail = UserFixture.email;

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(likeRepository.existsByPostIdAndUserId(post.getPostId(), user.getUserId())).thenReturn(false);
        when(postService.getLikeCount(user.getUserId(), post.getPostId())).thenReturn(1);

        likeService.addLike(post.getPostId(), userEmail);

        Optional<Like> like = likeRepository.findByPostIdAndUserId(post.getPostId(), user.getUserId());
        int likeCount = postService.getLikeCount(user.getUserId(), post.getPostId());

        assertNotNull(like);
        assertThat(likeCount).isEqualTo(1);

    }

    @Test
    @DisplayName("예외 테스트 : 이미 좋아요된 게시글 테스트")
    public void addLikePostWithAlreadyLikedPostTest() {

        User user = mock(User.class);
        Post post = mock(Post.class);
        String userEmail = UserFixture.email;

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(likeRepository.existsByPostIdAndUserId(post.getPostId(), user.getUserId())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                likeService.addLike(post.getPostId(), userEmail));
        assertEquals(ErrorCode.ALREADY_LIKED_POST, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 좋아요 취소 테스트")
    public void removeLikePostTest() {

        User user = mock(User.class);
        Post post = mock(Post.class);
        Like like = mock(Like.class);
        String userEmail = UserFixture.email;

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(likeRepository.findByPostIdAndUserId(post.getPostId(), user.getUserId())).thenReturn(Optional.ofNullable(like));

        likeService.removeLike(post.getPostId(), userEmail);

        Optional<Like> removeLike = likeRepository.findByPostIdAndUserId(post.getPostId(), user.getUserId());
        int likeCount = postService.getLikeCount(user.getUserId(), post.getPostId());

        assertNotNull(removeLike);
        assertThat(likeCount).isEqualTo(0);
    }

    @Test
    @DisplayName("예외 테스트 : 좋아요 되지않은 게시글 테스트")
    public void findEmailNotMatchRequestTest() {

        User user = mock(User.class);
        Post post = mock(Post.class);
        String userEmail = UserFixture.email;

        when(userService.getUserByEmail(userEmail)).thenReturn(user);
        when(likeRepository.findByPostIdAndUserId(post.getPostId(), user.getUserId())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                likeService.removeLike(post.getPostId(), userEmail));
        assertEquals(ErrorCode.NOT_LIKED_POST, exception.getErrorCode());


    }
}

