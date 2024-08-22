package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByLocationRequest;
import com.WearWeather.wear.domain.post.dto.response.*;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.service.TagService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final PostTagService postTagService;
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final LocationService locationService;
    private final TagService tagService;
    private final PostImageService postImageService;
    private final LikeRepository likeRepository;
    private static final String SORT_COLUMN_BY_CREATE_AT = "createAt";
    private static final String SORT_COLUMN_BY_LIKE_COUNT = "likeCount";

    @Transactional
    public Long createPost(String email, PostCreateRequest request) {
        User user = userService.getUserByEmail(email);
        Post post = request.toEntity(user.getUserId());

        postRepository.save(post);

        updateImagesInPost(request, post);
        postTagService.saveAllTag(post.getId(), request);

        return post.getId();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request) {
        Post post = findById(postId);
        post.modifyPostAttributes(request.getTitle(), request.getContent(), request.getLocation());

        updateImagesInPost(request, post);

        postTagService.deleteTagsByPost(postId);
        postTagService.saveAllTag(postId, request);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findById(postId);
        postRepository.delete(post);
    }

    @Transactional
    private void updateImagesInPost(PostImageRequest request, Post post) {
        List<PostImage> postImages = postImageService.findPostImagesByIdIn(request.getImageId());

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

    public List<TopLikedPostDetailResponse> getTopLikedPosts(String email) {
        User user = userService.getUserByEmail(email);
        List<Post> posts = getPostsOrderByLikeCountDesc();

        return posts.stream()
            .map(post -> getTopLikedPostDetail(post, user.getUserId()))
            .collect(Collectors.toList());
    }

    public List<Post> getPostsOrderByLikeCountDesc() {
        List<Long> postIds = likeRepository.findMostLikedPostIdForDay();

        return postRepository.findAllByIdInOrderByLikeCountDesc(postIds);
    }

    public TopLikedPostDetailResponse getTopLikedPostDetail(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<Long>> tags = getTagsByPostId(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return TopLikedPostDetailResponse.of(
            post,
            url,
            location,
            tags,
            like);
    }

    public PostDetailResponse getPostDetail(String email, Long postId) {
        User user = userService.getUserByEmail(email);
        String postUserNickname = userService.getNicknameById(user.getUserId());

        Post post = findById(postId);
        List<String> imageUrlList = getImageUrlList(post.getId());
        Map<String, List<Long>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), user.getUserId());

        return PostDetailResponse.of(
            postUserNickname,
            post,
            imageUrlList,
            tags,
            like);
    }

    public List<String> getImageUrlList(Long postId) {
        List<PostImage> postImages = postImageService.findPostImagesByPostId(postId);
        return postImages.stream()
            .map(image -> getImageUrl(image.getId()))
            .toList();
    }

    public String getImageUrl(Long thumbnailId) {
        PostImage postImage = postImageService.findPostImageById(thumbnailId);

        return awsS3Service.getUrl(postImage.getName());
    }

    public Map<String, List<Long>> getTagsByPostId(Long postId) {
        List<PostTag> postTags = postTagService.findPostTagsByPostId(postId);

        List<Long> tagIds = postTags.stream()
            .map(PostTag::getTagId)
            .collect(Collectors.toList());

        List<Tag> tags = tagService.findTagsById(tagIds);

        return tags.stream()
            .collect(Collectors.groupingBy(
                Tag::getCategory,
                Collectors.mapping(Tag::getTagId, Collectors.toList())
            ));
    }

    public boolean checkLikeByPostAndUser(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public PostsByLocationResponse getPostsByLocation(String email, PostsByLocationRequest request) {
        User user = userService.getUserByEmail(email);
        List<PostDetailByLocationResponse> responses = getPostDetailByLocation(request, user.getUserId());

        return PostsByLocationResponse.of(request.getLocation(), responses);
    }

    public List<PostDetailByLocationResponse> getPostDetailByLocation(PostsByLocationRequest request, Long userId) {
        String sortType = getSortColumnName(request.getSort());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortType).descending());
        Page<Post> posts = postRepository.findAllByLocation(pageable, request.getLocation());

        return posts.stream()
            .map(post -> getPostDetailByLocation(post, userId))
            .toList();
    }

    public String getSortColumnName(SortType sortType) {
        if (Objects.equals(sortType, SortType.LATEST)) {
            return SORT_COLUMN_BY_CREATE_AT;
        }

        if (Objects.equals(sortType, SortType.RECOMMENDED)) {
            return SORT_COLUMN_BY_LIKE_COUNT;
        }

        return SORT_COLUMN_BY_CREATE_AT;
    }

    public PostDetailByLocationResponse getPostDetailByLocation(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());
        Map<String, List<Long>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return PostDetailByLocationResponse.of(
            post.getId(),
            url,
            tags,
            like
        );
    }
}