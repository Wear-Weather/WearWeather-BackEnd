package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostDetailResponse;
import com.WearWeather.wear.domain.post.dto.request.PostsByLocationRequest;
import com.WearWeather.wear.domain.post.dto.response.*;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.domain.tag.service.TagService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final PostImageRepository postImageRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public Long createPost(String email, PostCreateRequest request) {
        User user = userService.getUserByEmail(email);
        Post post = request.toEntity(user.getUserId());

        postRepository.save(post);

        addImagesToPost(request, post);
        tagService.saveTags(post, request);

        return post.getPostId();
    }

    private void addImagesToPost(PostCreateRequest request, Post post) {
        List<PostImage> postImages = postImageRepository.findByIdIn(request.getImageId());

        for (int i = 0; i < postImages.size(); i++) {
            PostImage postImage = postImages.get(i);
            if (postImage.getPost() != null) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_IMAGE);
            }
            post.addPostImages(postImage);

            // 첫 번째 이미지를 대표 이미지로 설정
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
    public void incrementLikeCount(Long postId){
        Post post = findById(postId);
        post.updateLikeCount();
    }

    @Transactional
    public void removeLikeCount(Long postId) {
        Post post = findById(postId);
        post.removeLikeCount();
    }

    public Post findById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public List<TopLikedPostDetailResponse> getTopLikedPosts(String email){

        User user = userService.getUserByEmail(email);

        List<Post> posts = getPostsOrderByLikeCountDesc();

        return posts.stream()
                .map(post -> getTopLikedPostDetail(post, user.getUserId()))
                .collect(Collectors.toList());
    }

    public List<Post> getPostsOrderByLikeCountDesc(){

        List<Long> postIds = likeRepository.findMostLikedPostIdForDay();
        return postRepository.findAllByPostIdInOrderByLikeCountDesc(postIds);
    }

    public TopLikedPostDetailResponse getTopLikedPostDetail(Post post, Long userId){

        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<Long>> tags = getTagsByPostId(post.getPostTags());
        Long seasonTagId = getTagId(tags, "SEASON");
        List<Long> weatherTagIds = getTagIds(tags, "WEATHER");
        List<Long> temperatureTagIds =  getTagIds(tags, "TEMPERATURE");

        boolean like = checkLikeByPostAndUser(post.getPostId(), userId);

        return TopLikedPostDetailResponse.of(
                post,
                url,
                seasonTagId,
                weatherTagIds,
                temperatureTagIds,
                like);
    }

    public PostDetailResponse getPostDetail(String email, Long postId) {

        User user = userService.getUserByEmail(email);

        Post post = findById(postId);
        String postNickname = userService.getNicknameById(user.getUserId());

        List<String> imageUrlList = getImageUrlList(post.getPostImages());

        Map<String, List<Long>> tags = getTagsByPostId(post.getPostTags());
        Long seasonTagId = getTagId(tags, "SEASON");
        List<Long> weatherTagIds = getTagIds(tags, "WEATHER");
        List<Long> temperatureTagIds =  getTagIds(tags, "TEMPERATURE");

        boolean like = checkLikeByPostAndUser(post.getPostId(), user.getUserId());

        return PostDetailResponse.of(
                postNickname,
                post,
                imageUrlList,
                seasonTagId,
                weatherTagIds,
                temperatureTagIds,
                like);
    }

    public List<String> getImageUrlList(List<PostImage> postImages){
        return postImages.stream()
                .map(image -> getImageUrl(image.getId()))
                .toList();
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

    public PostsByLocationResponse getPostsByLocation(String email, PostsByLocationRequest request) {

        User user = userService.getUserByEmail(email);

        List<PostDetailByLocationResponse> responses = getPostDetailByLocation(request, user.getUserId());

        return PostsByLocationResponse.of(request.getLocation(), responses);
    }

    public List<PostDetailByLocationResponse> getPostDetailByLocation(PostsByLocationRequest request, Long userId){

        String sortType = getSortColumnName(request.getSort());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortType).descending());
        Page<Post> posts = postRepository.findAllByLocation(pageable, request.getLocation());

        return posts.stream()
                .map(post -> getPostDetailByLocation(post, userId))
                .toList();
    }

    public String getSortColumnName(SortType sortType){

        String latest = "createAt";
        String recommended = "likeCount";

        if(Objects.equals(sortType, SortType.LATEST)){
            return latest;
        }

        if(Objects.equals(sortType, SortType.RECOMMENDED)){
            return recommended;
        }

        return latest;
    }

    public PostDetailByLocationResponse getPostDetailByLocation(Post post, Long userId){

        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<Long>> tags = getTagsByPostId(post.getPostTags());
        Long seasonTagId = getTagId(tags, "SEASON");
        List<Long> weatherTagIds = getTagIds(tags, "WEATHER");
        List<Long> temperatureTagIds =  getTagIds(tags, "TEMPERATURE");

        boolean like = checkLikeByPostAndUser(post.getPostId(), userId);

        return PostDetailByLocationResponse.of(
                post.getPostId(),
                url,
                seasonTagId,
                weatherTagIds,
                temperatureTagIds,
                like
        );
    }

    public Long getTagId(Map<String, List<Long>> tags, String category){
        return tags.get(category).get(0);
    }

    public List<Long> getTagIds(Map<String, List<Long>> tags, String category){
        return tags.get(category);
    }
}

