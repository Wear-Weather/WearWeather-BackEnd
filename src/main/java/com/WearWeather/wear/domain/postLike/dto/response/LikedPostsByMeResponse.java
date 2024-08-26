package com.WearWeather.wear.domain.postLike.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record LikedPostsByMeResponse(
        List<LikedPostByMeResponse> myLikedPosts
) {
    public static LikedPostsByMeResponse of(List<LikedPostByMeResponse> likedPosts){
        return LikedPostsByMeResponse.builder()
                .myLikedPosts(likedPosts)
                .build();
    }
}
