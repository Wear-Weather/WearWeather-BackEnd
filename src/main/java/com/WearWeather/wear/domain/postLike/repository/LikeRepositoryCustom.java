package com.WearWeather.wear.domain.postLike.repository;

import java.util.List;

public interface LikeRepositoryCustom {
        List<Long> findMostLikedPostIdForDay();
}
