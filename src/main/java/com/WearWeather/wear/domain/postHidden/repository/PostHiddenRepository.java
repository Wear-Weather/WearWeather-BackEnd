package com.WearWeather.wear.domain.postHidden.repository;

import com.WearWeather.wear.domain.postHidden.entity.PostHidden;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostHiddenRepository extends JpaRepository<PostHidden, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    List<PostIdMapping> findAllByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM PostHidden ph WHERE ph.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
