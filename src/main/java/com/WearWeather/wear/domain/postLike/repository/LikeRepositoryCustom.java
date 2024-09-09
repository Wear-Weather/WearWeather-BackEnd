package com.WearWeather.wear.domain.postLike.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LikeRepositoryCustom {
        List<Long> findMostLikedPostIdForDay(List<Long> hiddenPostIds);

        Page<Long> findByUserId(Long userId, Pageable pageable);
}
