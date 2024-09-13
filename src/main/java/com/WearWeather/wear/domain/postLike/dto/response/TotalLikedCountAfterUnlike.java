package com.WearWeather.wear.domain.postLike.dto.response;

import lombok.Builder;

@Builder
public record TotalLikedCountAfterUnlike(
        int likedCount
) {
    public static TotalLikedCountAfterUnlike of(int likedCount){
        return TotalLikedCountAfterUnlike.builder()
                .likedCount(likedCount)
                .build();
    }
}
