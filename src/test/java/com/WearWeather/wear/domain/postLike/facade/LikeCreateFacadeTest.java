package com.WearWeather.wear.domain.postLike.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeCreateFacadeTest {

        @InjectMocks
        LikeCreateFacade likeCreateFacade;

        @Mock
        PostService postService;

        @Mock
        LikeService likeService;

        @Test
        @DisplayName("정상 테스트: 좋아요 추가")
        public void addLikePostTest() {
            Long userId = 1L;
            Long postId = 1L;

            doNothing().when(postService).validatePostExists(postId);
            doNothing().when(likeService).save(userId, postId);
            when(postService.incrementLikeCount(postId)).thenReturn(10);

            TotalLikedCountAfterLike result = likeCreateFacade.addLike(userId, postId);

            verify(postService).validatePostExists(postId);
            verify(likeService).save(userId, postId);
            verify(postService).incrementLikeCount(postId);

            assertEquals(10, result.likedCount());
        }

}
