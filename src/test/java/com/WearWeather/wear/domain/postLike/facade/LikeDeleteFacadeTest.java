package com.WearWeather.wear.domain.postLike.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterUnlike;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeDeleteFacadeTest {

        @InjectMocks
        LikeDeleteFacade likeDeleteFacade;

        @Mock
        PostService postService;

        @Mock
        LikeService likeService;

        @Test
        @DisplayName("정상 테스트: 좋아요 취소")
        public void removeLikePostTest() {
            Long userId = 1L;
            Long postId = 1L;

            doNothing().when(postService).validatePostExists(postId);
            doNothing().when(likeService).delete(userId, postId);
            when(postService.removeLikeCount(postId)).thenReturn(5);

            TotalLikedCountAfterUnlike result = likeDeleteFacade.removeLike(userId, postId);

            verify(postService).validatePostExists(postId);
            verify(likeService).delete(userId, postId);
            verify(postService).removeLikeCount(postId);

            assertEquals(5, result.likedCount());
        }

        @Test
        @DisplayName("예외 테스트: 좋아요되지 않은 게시글 취소")
        public void removeLikeNotLikedPostTest() {
            Long userId = 1L;
            Long postId = 1L;

            doNothing().when(postService).validatePostExists(postId);
            doThrow(new CustomException(ErrorCode.NOT_LIKED_POST)).when(likeService).delete(userId, postId);

            CustomException exception = assertThrows(CustomException.class, () ->
              likeDeleteFacade.removeLike(userId, postId)
            );

            verify(postService).validatePostExists(postId);
            verify(likeService).delete(userId, postId);
            assertEquals(ErrorCode.NOT_LIKED_POST, exception.getErrorCode());
        }

}
