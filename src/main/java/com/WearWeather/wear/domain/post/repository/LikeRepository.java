package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostIdAndUserId(Long postId, long userId);
}
