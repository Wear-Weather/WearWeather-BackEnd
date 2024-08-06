package com.WearWeather.wear.domain.postImage.service;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;

    @Transactional
    public Long createPostImage(MultipartFile multipartFile, ImageInfoDto imageInfoDto) {

        PostImage postImage = PostImage.builder()
            .post(null)
            .name(imageInfoDto.getS3Name())
            .originName(multipartFile.getOriginalFilename())
            .byteSize((int) multipartFile.getSize())
            .width(imageInfoDto.getWidth())
            .height(imageInfoDto.getHeight())
            .build();

        return postImageRepository.save(postImage).getId();
    }
}
