package com.WearWeather.wear.domain.storage.controller;

import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.domain.storage.dto.ImageInfoResponse;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;
    private final PostImageService postImageService;

    @PostMapping("/post-image")
    public ResponseEntity<ImageInfoResponse> upload(@RequestPart("file") MultipartFile multipartFile) {
        ImageInfoDto imageInfoDto = awsS3Service.upload(multipartFile, "post-image");
        Long postImageId = postImageService.createPostImage(multipartFile, imageInfoDto);
        return ResponseEntity.ok(ImageInfoResponse.of(postImageId, imageInfoDto.getUrl()));
    }

    @DeleteMapping("/post-image/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long imageId) {
        postImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
