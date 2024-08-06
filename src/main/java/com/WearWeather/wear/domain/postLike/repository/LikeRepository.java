package com.WearWeather.wear.domain.postLike.repository;

import com.WearWeather.wear.domain.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    boolean existsByPostIdAndUserId(Long postId, long userId);

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}
