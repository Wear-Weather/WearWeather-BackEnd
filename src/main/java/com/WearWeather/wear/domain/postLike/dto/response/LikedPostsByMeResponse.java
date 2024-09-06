package com.WearWeather.wear.domain.postLike.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record LikedPostsByMeResponse(
        List<LikedPostByMeResponse> myLikedPosts,
        int total
) {
    public static LikedPostsByMeResponse of(List<LikedPostByMeResponse> likedPosts, int totalPage){
        return LikedPostsByMeResponse.builder()
                .myLikedPosts(likedPosts)
                .total(totalPage)
                .build();
    }
}
