package com.WearWeather.wear.domain.postImage.service;

import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public void savePostIdInImages(Post post,PostImageRequest request) {
        List<PostImage> postImages = postImageRepository.findByIdIn(request.getImageIds());

        for (int i = 0; i < postImages.size(); i++) {
            PostImage postImage = postImages.get(i);
            if (postImage.getPostId() != null) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_IMAGE);
            }
            postImage.updatePostId(post.getId());

            if (i == 0) {
                post.addThumbnailImageId(postImage.getId());
            }
        }
    }

    @Transactional
    public void updatePostImages(Post post, PostUpdateRequest request) {
        List<Long> existingImageIds = postImageRepository.findImageIdsByPostId(post.getId());

        for (Long imageId : request.getImageIds()) {
            if (!existingImageIds.contains(imageId)) {
                PostImage postImage = getValidatedImage(imageId);
                postImage.updatePostId(post.getId());
            }
        }

        if (!request.getImageIds().isEmpty()) {
            post.addThumbnailImageId(request.getImageIds().get(0));
        }
    }

    private PostImage getValidatedImage(Long imageId) {
        PostImage postImage = postImageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        if (postImage.getPostId() != null) {
            throw new CustomException(ErrorCode.IMAGE_ID_ALREADY_ASSOCIATED_WITH_POST);
        }

        return postImage;
    }

    @Transactional
    public void deleteImage(Long imageId) {
        PostImage postImage = postImageRepository.findById(imageId)
            .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        awsS3Service.delete(postImage.getName());
        postImageRepository.delete(postImage);
    }

    @Transactional
    public void deleteImagesByPostId(Long postId) {
        List<PostImage> postImages = postImageRepository.findByPostId(postId);

        for (PostImage postImage : postImages) {
            awsS3Service.delete(postImage.getName());
        }

        postImageRepository.deleteByPostId(postId);
    }

    @Transactional
    public void deleteUnNecessaryImage() {
        final List<PostImage> images = postImageRepository.findByPostIdIsNull();

        images.stream()
            .filter(image -> Duration.between(image.getCreatedAt(), LocalDateTime.now()).toHours() >= 24)
            .forEach(image -> {
                awsS3Service.delete(image.getName());
                postImageRepository.delete(image);
            });
    }
}
