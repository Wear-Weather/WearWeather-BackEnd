package com.WearWeather.wear.domain.postLike.repository;

import com.WearWeather.wear.domain.postLike.entity.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query("DELETE FROM Like li WHERE li.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

}
