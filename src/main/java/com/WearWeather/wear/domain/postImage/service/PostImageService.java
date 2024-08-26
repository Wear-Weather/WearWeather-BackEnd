package com.WearWeather.wear.domain.postImage.service;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public Long createPostImage(MultipartFile multipartFile, ImageInfoDto imageInfoDto) {
        PostImage postImage = PostImage.builder()
            .postId(null)
            .name(imageInfoDto.getS3Name())
            .originName(multipartFile.getOriginalFilename())
            .byteSize((int) multipartFile.getSize())
            .width(imageInfoDto.getWidth())
            .height(imageInfoDto.getHeight())
            .build();

        return postImageRepository.save(postImage).getId();
    }

    @Transactional
    public void deletePostImage(Long imageId) {
        PostImage postImage = postImageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        // S3에서 이미지 삭제
        awsS3Service.delete(postImage.getName());

        // DB에서 이미지 정보 삭제
        postImageRepository.delete(postImage);
    }

    public List<PostImage> findPostImagesByPostId(Long postId){
         return postImageRepository.findByPostId(postId);
    }

    public PostImage findPostImageById(Long thumbnailId){
         return postImageRepository.findById(thumbnailId)
                 .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST_IMAGE));
    }

    public List<PostImage> findPostImagesByIdIn(List<Long> imageIds){
        return postImageRepository.findByIdIn(imageIds);
    }
}

