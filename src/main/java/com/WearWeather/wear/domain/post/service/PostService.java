package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.response.*;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.dto.request.PostImageRequest;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
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
    private final TagRepository tagRepository;
    private final PostTagService postTagService;
    private final PostImageRepository postImageRepository;
    private final UserService userService;
    private final LikeRepository likeRepository;
    private final AwsS3Service awsS3Service;
    private final PostTagRepository postTagRepository;
    private final LocationService locationService;

    private static final String SORT_COLUMN_BY_CREATE_AT = "createdAt";
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
        List<PostImage> postImages = postImageRepository.findByIdIn(request.getImageId());

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

    public List<TopLikedPostResponse> getTopLikedPosts(String email) {
        User user = userService.getUserByEmail(email);
        List<Post> posts = getPostsOrderByLikeCountDesc();

        return posts.stream()
            .map(post -> getTopLikedPost(post, user.getUserId()))
            .collect(Collectors.toList());
    }

    public List<Post> getPostsOrderByLikeCountDesc() {
        List<Long> postIds = likeRepository.findMostLikedPostIdForDay();

        return postRepository.findAllByIdInOrderByLikeCountDesc(postIds);
    }

    public TopLikedPostResponse getTopLikedPost(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<Long>> tags = getTagsByPostId(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return TopLikedPostResponse.of(
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
        ImagesResponse imageUrlList = getImagesResponse(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<Long>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), user.getUserId());
        boolean report = false; //TODO : 신고하기 완성 후 수정

        return PostDetailResponse.of(
            postUserNickname,
            post,
            imageUrlList,
            location,
            tags,
            like,
            report);
    }

    public ImagesResponse getImagesResponse(Long postId) {
        return ImagesResponse.of(getImageDetailResponseList(postId));
    }

    public List<ImageDetailResponse> getImageDetailResponseList(Long postId) {
        List<PostImage> postImages = postImageRepository.findByPostId(postId);

        return postImages.stream()
            .map(image -> ImageDetailResponse.of(image.getId(), getImageUrl(image.getId())))
            .toList();
    }

    public String getImageUrl(Long thumbnailId) {
        PostImage postImage = postImageRepository.findById(thumbnailId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST_IMAGE));

        return awsS3Service.getUrl(postImage.getName());
    }

    public Map<String, List<Long>> getTagsByPostId(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);

        List<Long> tagIds = postTags.stream()
            .map(PostTag::getTagId)
            .collect(Collectors.toList());

        List<Tag> tags = tagRepository.findAllById(tagIds);

        return tags.stream()
            .collect(Collectors.groupingBy(
                Tag::getCategory,
                Collectors.mapping(Tag::getTagId, Collectors.toList())
            ));
    }

    public boolean checkLikeByPostAndUser(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public PostsByLocationResponse getPostsByLocation(String email, int page, int size, String city, String district, SortType sort) {
        User user = userService.getUserByEmail(email);

        Location location = locationService.findCityIdAndDistrictId(city, district);
        List<PostByLocationResponse> responses = getPostByLocation(page, size, location, sort, user.getUserId());

        return PostsByLocationResponse.of(LocationResponse.of(city, district), responses);
    }

    public List<PostByLocationResponse> getPostByLocation(int page, int size, Location location, SortType sort, Long userId) {
        String sortType = getSortColumnName(sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortType).descending());
        Page<Post> posts = postRepository.findAllByLocation(pageable, location);

        return posts.stream()
            .map(post -> getPostByLocation(post, userId))
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

    public PostByLocationResponse getPostByLocation(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());
        Map<String, List<Long>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);
        boolean report = false; //TODO : 신고하기 완성 후 수정

        return PostByLocationResponse.of(
            post.getId(),
            url,
            tags,
            like,
            report
        );
    }

    public PostsByFiltersResponse searchPostsWithFilters(String email, PostsByFiltersRequest request) {
        User user = userService.getUserByEmail(email);

        List<SearchPostResponse> responses = getPostByFilters(request, user.getUserId());

        return PostsByFiltersResponse.of(responses);
    }

    public List<SearchPostResponse> getPostByFilters(PostsByFiltersRequest request, Long userId){
        //TODO : getPostDetailByLocation()메서드랑 중복 제거하기

        String sortType = getSortColumnName(request.getSort());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortType).descending());
        Page<PostWithLocationName> posts = postRepository.findPostsByFilters(request, pageable);

        return posts.stream()
                .map(post -> getPostByFilters(post, userId))
                .toList();
    }

    public SearchPostResponse getPostByFilters(PostWithLocationName post, Long userId){

        String url = getImageUrl(post.thumbnailImageId());

        Map<String, List<Long>> tags =  getTagsByPostId(post.postId());

        boolean like = checkLikeByPostAndUser(post.postId(), userId);

        boolean report = false; //TODO : 신고 테이블 완성 후 수정

        return SearchPostResponse.of(
                post,
                url,
                tags,
                like,
                report
        );
    }

    public PostsByMeResponse getPostsByMe(String email, int page, int size){
        User user = userService.getUserByEmail(email);

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findByUserId(user.getUserId(), pageable);

        List<PostByMeResponse> postByMe = posts.stream()
                .map(this::getPostByMe)
                .toList();

        return PostsByMeResponse.of(postByMe);
    }

    private PostByMeResponse getPostByMe(Post post) {

        String url = getImageUrl(post.getThumbnailImageId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<Long>> tags = getTagsByPostId(post.getId());
        boolean report = false; //TODO : 신고하기 기능 완성 후 수정

        return PostByMeResponse.of(
                post.getId(),
                url,
                location,
                tags,
                report
        );
    }
}