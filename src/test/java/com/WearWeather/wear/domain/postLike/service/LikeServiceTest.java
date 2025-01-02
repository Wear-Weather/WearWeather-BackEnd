package com.WearWeather.wear.domain.postLike.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.facade.LikeCreateFacade;
import com.WearWeather.wear.domain.postLike.facade.LikeDeleteFacade;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

        @InjectMocks
        LikeCreateFacade likeCreateFacade;

        @InjectMocks
        LikeDeleteFacade likeDeleteFacade;

        @Mock
        PostService postService;

        @InjectMocks
        LikeService likeService;

        @Mock
        LikeRepository likeRepository;

        @Test
        @DisplayName("save: 예외 테스트 - 이미 좋아요된 게시글")
        public void saveThrowsException() {
            Long userId = 1L;
            Long postId = 1L;

            when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

            CustomException exception = assertThrows(CustomException.class, () -> {
                likeService.save(userId, postId);
            });

            verify(likeRepository).existsByPostIdAndUserId(postId, userId);
        }

}
