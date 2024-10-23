package com.WearWeather.wear.global.common;

import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postLike.entity.Like;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikeCronTable {

  private final LikeRepository likeRepository;
  private final PostRepository postRepository;
  private final PostService postService;

  @Value("${schedule.like.use}")
  private boolean likeSchedule;

  @Scheduled(cron = "${schedule.like.cron}")
  public void setLikeSchedule() {

    if (likeSchedule) {
      int totalLikeCount = 15;
      List<Long> postIdsList = postRepository.find20IdsByOrderByIdDesc();

      Random random = new Random();

      List<Long> userIdList = Arrays.asList(11L, 17L, 21L, 63L, 64L);

      List<Like> likeList = postIdsList.stream()
          .flatMap(postId -> {
                int randomCount = random.nextInt(3);

                return randomCount == 0 ? Stream.empty() : Stream.generate(() -> {

                      int randomIndex = random.nextInt(userIdList.size());

                      //임시 추가
                      postService.incrementLikeCount(postId);

                      return Like.builder()
                          .userId(userIdList.get(randomIndex))
                          .postId(postId)
                          .build();
                    })
                    .limit(randomCount);
              }
          )
          .limit(totalLikeCount)
          .toList();

      likeRepository.saveAll(likeList);
    }
  }
}