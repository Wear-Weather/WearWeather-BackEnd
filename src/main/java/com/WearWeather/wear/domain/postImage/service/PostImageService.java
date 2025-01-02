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
import java.util.Optional;
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
          .build();

        return postImageRepository.save(postImage).getId();
    }

    @Transactional
    public void mappingPostIdInImages(Post post, PostImageRequest request) {
        List<PostImage> postImages = getPostImages(request.getImageIds());

        for (PostImage postImage : postImages) {
            mappingPost(postImage,post);
        }

        assignThumbnailImage(post, postImages);
    }

    public List<PostImage> getPostImages(List<Long> imageIds) {
        return Optional.ofNullable(postImageRepository.findByIdIn(imageIds))
          .filter(images -> !images.isEmpty())
          .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
    }

    public void mappingPost(PostImage postImage,Post post) {
        validatePostImageAssociation(postImage);
        postImage.updatePostId(post.getId());
    }

    public void validatePostImageAssociation(PostImage postImage) {
        if (postImage.getPostId() != null) {
            throw new CustomException(ErrorCode.IMAGE_ID_ALREADY_ASSOCIATED_WITH_POST);
        }
    }

    private void assignThumbnailImage(Post post, List<PostImage> postImages) {
            post.addThumbnailImageId(postImages.get(0).getId());
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

    public String getImageUrl(Long thumbnailId) {
        PostImage postImage = postImageRepository.findById(thumbnailId)
          .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

       return awsS3Service.getUrl(postImage.getName());
    }

    public List<PostImage> getPostImagesByPost(Long postId) {
        return postImageRepository.findByPostId(postId);
    }
}
