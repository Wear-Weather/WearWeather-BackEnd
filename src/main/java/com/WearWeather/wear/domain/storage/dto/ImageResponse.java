package com.WearWeather.wear.domain.storage.dto;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ImageResponse {

    private String url;
    private int width;
    private int height;


    public static ImageResponse of(PostImage postImage) {
        return ImageResponse.builder()
            .url("https://weather-image.s3.ap-northeast-2.amazonaws.com" + postImage.getName()) // TODO : 추후 Util로 빼줄 예정
            .width(postImage.getWidth())
            .height(postImage.getHeight())
            .build();
    }
}