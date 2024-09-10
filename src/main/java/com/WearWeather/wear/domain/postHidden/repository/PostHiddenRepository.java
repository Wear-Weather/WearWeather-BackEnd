package com.WearWeather.wear.domain.postHidden.repository;

import com.WearWeather.wear.domain.postHidden.entity.PostHidden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostHiddenRepository extends JpaRepository<PostHidden, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);
    List<PostIdMapping> findAllByUserId(Long userId);
}
