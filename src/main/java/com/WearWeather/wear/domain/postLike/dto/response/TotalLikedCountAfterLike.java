package com.WearWeather.wear.domain.postLike.dto.response;

import lombok.Builder;

@Builder
public record TotalLikedCountAfterLike(
        int likedCount
) {
    public static TotalLikedCountAfterLike of(int likedCount){
        return TotalLikedCountAfterLike.builder()
                .likedCount(likedCount)
                .build();
    }
}
