package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostDetailResponse;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.domain.tag.service.TagService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final PostTagService postTagService;
    private final PostImageRepository postImageRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public Long createPost(String email, PostCreateRequest request) {
        User user = userService.getUserByEmail(email);
        Post post = request.toEntity(user.getUserId());

        addImagesToPost(request, post);
        postTagService.saveAllTag(post, request);

        return post.getPostId();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = findById(postId);
        post.modifyPostAttributes(request.getTitle(), request.getContent(), request.getLocation());

        addImagesToPost(request, post);

        postTagService.deleteTagsByPost(post);
        post.getPostTags().clear();

        postTagService.saveAllTag(post, request);
    }

    @Transactional
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

    public List<TopLikedPostDetailResponse> getTopLikedPosts(String email){

        User user = userService.getUserByEmail(email);

        List<Post> posts = getPostsOrderByLikeCountDesc();

        return posts.stream()
                .map(post -> toGetPostDetailResponse(post, user.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Post> getPostsOrderByLikeCountDesc(){

        List<Long> postIds = likeRepository.findMostLikedPostIdForDay();
        return postRepository.findAllByPostIdInOrderByLikeCountDesc(postIds);
    }

    public TopLikedPostDetailResponse toGetPostDetailResponse(Post post, Long userId){

        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<Long>> tags = getTagsByPostId(post.getPostTags());
        Long seasonTag = tags.get("SEASON").get(0);
        List<Long> weatherTags = tags.get("WEATHER");
        List<Long> temperatureTags = tags.get("TEMPERATURE");

        boolean like = checkLikeByPostAndUser(post.getPostId(), userId);

        return TopLikedPostDetailResponse.of(
                post,
                url,
                seasonTag,
                weatherTags,
                temperatureTags,
                like);
    }

    public String getImageUrl(Long thumbnailId){
        PostImage postImage = postImageRepository.findById(thumbnailId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST_IMAGE));

        return awsS3Service.getUrl(postImage.getName());
    }

    public Map<String, List<Long>> getTagsByPostId(List<PostTag> postTags) {

        List<Long> tagIds = postTags.stream()
                .map(postTag -> postTag.getTag().getTagId())
                .collect(Collectors.toList());

        List<Tag> tags = tagRepository.findAllById(tagIds);

        return tags.stream()
                .collect(Collectors.groupingBy(
                        Tag::getCategory,
                        Collectors.mapping(Tag::getTagId, Collectors.toList())
                ));
    }

    public boolean checkLikeByPostAndUser(Long postId, Long userId){
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}