package com.WearWeather.wear.domain.postHidden.repository;

import com.WearWeather.wear.domain.postHidden.entity.PostHidden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHiddenRepository extends JpaRepository<PostHidden, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
