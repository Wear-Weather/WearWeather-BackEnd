package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostTagService postTagService;
    private final PostImageRepository postImageRepository;
    private final UserService userService;

    @Transactional
    public Long createPost(String email, PostCreateRequest request) {
        User user = userService.getUserByEmail(email);
        Post post = request.toEntity(user.getUserId());

        addImagesToPost(request, post);
        postTagService.saveAllTag(post, request);

        return post.getPostId();
    }

    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = findById(postId);
        post.modifyPostAttributes(request.getTitle(), request.getContent(), request.getLocation());

        addImagesToPost(request, post);

        postTagService.deleteTagsByPost(post);
        post.getPostTags().clear();

        postTagService.saveAllTag(post, request);
    }

    public void deletePost(Long postId) {
        Post post = findById(postId);
        postRepository.delete(post);
    }

    private void addImagesToPost(PostImageRequest request, Post post) {
        List<PostImage> postImages = postImageRepository.findByIdIn(request.getImageId());

        for (int i = 0; i < postImages.size(); i++) {
            PostImage postImage = postImages.get(i);
            if (postImage.getPost() != null) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_IMAGE);
            }
            post.addPostImages(postImage);

            if (i == 0) {
                post.addThumbnailImageId(postImage.getId());
            }
        }
    }

    public void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.NOT_EXIST_POST);
        }
    }

    @Transactional
    public void incrementLikeCount(Long postId) {
        Post post = findById(postId);
        post.updateLikeCount();
    }

    @Transactional
    public void removeLikeCount(Long postId) {
        Post post = findById(postId);
        post.removeLikeCount();
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }
}
