package com.WearWeather.wear.domain.postLike.repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LikeRepositoryCustom {
        List<Long> findMostLikedPostIdForDay(List<Long> hiddenPostIds);

        List<Long> findByUserId(Long userId, Pageable pageable);
}
