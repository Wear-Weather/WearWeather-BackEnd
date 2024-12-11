package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.ImageDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.ImagesResponse;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostWithLocationName;
import com.WearWeather.wear.domain.post.dto.response.PostsByFiltersResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.SearchPostResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.entity.Gender;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.weather.domain.OutfitGuideByTemperature;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostByMeResponse;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
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
    private final PostReportService postReportService;
    private final PostHiddenService postHiddenService;
    private final PostImageService postImageService;

    private static final String SORT_COLUMN_BY_CREATE_AT = "createdAt";
    private static final String SORT_COLUMN_BY_LIKE_COUNT = "likeCount";

    @Transactional
    public Long createPost(Long userId, PostCreateRequest request) {
        Location location = locationService.findCityIdAndDistrictId(request.getCity(),request.getDistrict());
        Post post = request.toEntity(userId,location);
        postRepository.save(post);

        postImageService.savePostIdInImages(post,request);
        postTagService.saveTags(post, request);

        return post.getId();
    }

    @Transactional
    public void updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Location location = locationService.findCityIdAndDistrictId(request.getCity(),request.getDistrict());
        Post post = validateUserPermission(userId, postId);

        post.updatePostDetails(request.getTitle(), request.getContent(),
            Gender.valueOf(request.getGender()), location);
        postImageService.updatePostImages(post, request);
        postTagService.updatePostTags(post,request);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = validateUserPermission(userId, postId);

        deleteRelatedDataByPostId(postId);
         postRepository.delete(post);
    }

    private void deleteRelatedDataByPostId(Long postId) {
        postImageService.deleteImagesByPostId(postId);
        postTagService.deleteTagsByPostId(postId);
        postHiddenService.deleteHiddenByPostId(postId);
        postReportService.deleteReportByPostId(postId);

        //TODO : 임시 Repository 코드 작성 -> 전체적인 프로젝트 구조 개선 필요
        likeRepository.deleteByPostId(postId);
    }

    private Post validateUserPermission(Long userId, Long postId) {
        Post post = findById(postId);
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return post;
    }

    public void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.NOT_EXIST_POST);
        }
    }

    @Transactional
    public Integer incrementLikeCount(Long postId) {
        Post post = findById(postId);
        return post.updateLikeCount();
    }

    @Transactional
    public Integer removeLikeCount(Long postId) {
        Post post = findById(postId);
        return post.removeLikeCount();
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public List<TopLikedPostResponse> getTopLikedPosts(Long userId) {
        // userId가 null이면 빈 리스트로 설정 (로그인하지 않은 사용자)
        List<Long> invisiblePostIdsList = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();

        List<Long> getPostIdsNotInHiddenPostIds = findMostLikedPostIdForDay(invisiblePostIdsList);
        List<Post> posts = getPostsOrderByPostIds(getPostIdsNotInHiddenPostIds);

        return posts.stream()
          .map(post -> getTopLikedPost(post, userId))
          .collect(Collectors.toList());
    }


    public List<Post> getPostsOrderByPostIds(List<Long> postIds) {
        List<Post> posts = postRepository.findAllByIdIn(postIds);

        return postIds.stream()
          .map(postId -> posts.stream()
            .filter(post -> post.getId().equals(postId))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST)))
          .toList();
    }

    public List<Long> findMostLikedPostIdForDay(List<Long> invisiblePostIdsList) {
        return likeRepository.findMostLikedPostIdForDay(invisiblePostIdsList); //TODO : 서비스 레이어 분리 후 likeService로의 의존으로 수정
    }

    public TopLikedPostResponse getTopLikedPost(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());

        Map<String, List<String>> tags = getTagsByPostId(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return TopLikedPostResponse.of(
            post,
            url,
            location,
            tags,
            like,
            post.getGender());
    }

    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = findById(postId);

        String postUserNickname = userService.getNicknameById(post.getUserId());
        ImagesResponse imageUrlList = getImagesResponse(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<String>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);
        boolean report = postReportService.hasReports(post.getId());

        return PostDetailResponse.of(
          postUserNickname,
          post,
          imageUrlList,
          location,
          tags,
          like,
          report,
          post.getGender());
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
          .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        return awsS3Service.getUrl(postImage.getName());
    }

    public Map<String, List<String>> getTagsByPostId(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);

        List<Long> tagIds = postTags.stream()
          .map(PostTag::getTagId)
          .collect(Collectors.toList());

        List<Tag> tags = tagRepository.findAllById(tagIds);

        return tags.stream()
          .collect(Collectors.groupingBy(
            Tag::getCategory,
            Collectors.mapping(Tag::getContent, Collectors.toList())
          ));
    }

    public boolean checkLikeByPostAndUser(Long postId, Long userId) {
        if (userId == null) {
            return false;
        }
        return likeRepository.existsByPostIdAndUserId(postId, userId);
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

    public PostsByFiltersResponse getPosts(Long userId, PostsByFiltersRequest request) {

        Page<PostWithLocationName> posts = getPostByFilters(request, userId);

        List<SearchPostResponse> responses = posts.stream()
          .map(post -> getPostByFilters(post, userId))
          .toList();
        int totalPage = posts.getTotalPages() - 1;

        return PostsByFiltersResponse.of(responses, totalPage);
    }

    public Page<PostWithLocationName> getPostByFilters(PostsByFiltersRequest request, Long userId) {
        String sortType = getSortColumnName(request.sort());

        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();

        Pageable pageable = PageRequest.of(request.page(), request.size(), Sort.by(sortType).descending());

        return postRepository.findPostsByFilters(request, pageable, invisiblePostIds);
    }

    public SearchPostResponse getPostByFilters(PostWithLocationName post, Long userId) {

        String url = getImageUrl(post.thumbnailImageId());

        Map<String, List<String>> tags = getTagsByPostId(post.postId());

        boolean like = checkLikeByPostAndUser(post.postId(), userId);

        return SearchPostResponse.of(
            post,
            url,
            tags,
            like,
            post.gender()
        );
    }

    public PostsByMeResponse getPostsByMe(Long userId, int page, int size) {

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findByUserId(userId, pageable);

        List<PostByMeResponse> postByMe = posts.stream()
          .map(post -> getPostByMe(userId, post))
          .toList();

        int totalPage = posts.getTotalPages() -1;
        return PostsByMeResponse.of(postByMe, totalPage);
    }

    private PostByMeResponse getPostByMe(Long userId, Post post) {

        String url = getImageUrl(post.getThumbnailImageId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<String>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);
        boolean report = postReportService.hasReports(post.getId());

        return PostByMeResponse.of(
            post.getId(),
            url,
            location,
            tags,
            like,
            report
        );
    }

    public List<LikedPostByMeResponse> getLikedPostsByMe(Long userId, Page<Long> likedPostIds) {

        List<Post> posts = postRepository.findAllById(likedPostIds);

        return posts.stream()
            .map(post -> getLikedPost(userId, post))
            .toList();
    }

    public LikedPostByMeResponse getLikedPost(Long userId, Post post) {

        String url = getImageUrl(post.getThumbnailImageId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());

        Map<String, List<String>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return LikedPostByMeResponse.of(
            post.getId(),
            url,
            location,
            tags,
            like
        );
    }

    public List<Long> getInvisiblePostIdsList(Long userId){
        List<Long> hiddenPostIds = findHiddenPostsByUserId(userId);
        List<Long> reportedByMePostIds = findReportedPostsByUserId(userId);
        List<Long> reportedPostIds = findMoreFiveReportedPosts();

        return distinctMergedPostIdsList(hiddenPostIds, reportedByMePostIds, reportedPostIds);
    }

    public List<Long> findHiddenPostsByUserId(Long userId){
        return postHiddenService.findHiddenPostsByUserId(userId);
    }

    public List<Long> findReportedPostsByUserId(Long userId){
        return postReportService.findReportedPostsByUserId(userId);
    }

    public List<Long> findMoreFiveReportedPosts(){
        return postReportService.findPostsExceedingReportCount();
    }

    public List<Long> distinctMergedPostIdsList(List<Long> hiddenPostIds, List<Long> reportedByMePostIds, List<Long> reportedPostIds){
        Set<Long> postIdsSet = new LinkedHashSet<>(hiddenPostIds);
        postIdsSet.addAll(reportedByMePostIds);
        postIdsSet.addAll(reportedPostIds);
        return new ArrayList<>(postIdsSet);
    }

    public PostsByTemperatureResponse getPostsByTemperature(Long userId, int tmp, int page, int size) {

        OutfitGuideByTemperature outfitGuideByTemperature = OutfitGuideByTemperature.fromTemperature(tmp);

        int tmpRangeStart = outfitGuideByTemperature.getRangeStart();
        int tmpRangeEnd = outfitGuideByTemperature.getRangeEnd();

        Page<Post> posts = getPostByTemperature(userId, tmpRangeStart, tmpRangeEnd, page, size);

        List<PostByTemperatureResponse> responses = posts.stream()
            .map(post -> getPostByTemperature(post, userId))
            .toList();

        int totalPages = posts.getTotalPages() - 1;

        return PostsByTemperatureResponse.of(tmpRangeStart, tmpRangeEnd, responses, totalPages);
    }

    public Page<Post> getPostByTemperature(Long userId, int rangeStart, int rangeEnd, int page, int size) {

        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();

        Pageable pageable = PageRequest.of(page, size);

        return postRepository.findPostsByTmp(rangeStart, rangeEnd, pageable, invisiblePostIds);
    }

    public PostByTemperatureResponse getPostByTemperature(Post post, Long userId) {

        String url = getImageUrl(post.getThumbnailImageId());
        Map<String, List<String>> tags = getTagsByPostId(post.getId());
        LocationResponse location = locationService.findCityIdAndDistrictId(post.getLocation().getCity(), post.getLocation().getDistrict());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return PostByTemperatureResponse.of(
            post.getId(),
            url,
            location,
            tags,
            like
        );
    }

    public PostsByLocationResponse getPostsByLocation(Long userId, int page, int size, String city, String district, SortType sort) {
        Location location = locationService.findCityIdAndDistrictId(city, district);
        Page<Post> posts = getPostByLocation(page, size, location, sort, userId);

        List<PostByLocationResponse> responses = posts.stream()
            .map(post -> getPostByLocation(post, userId))
            .toList();

        int totalPages = posts.getTotalPages() - 1;

        return PostsByLocationResponse.of(LocationResponse.of(city, district), responses, totalPages);
    }


    public Page<Post> getPostByLocation(int page, int size, Location location, SortType sort, Long userId) {
        String sortType = getSortColumnName(sort);

        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortType).descending());

        return postRepository.getPostsExcludingInvisiblePosts(pageable, location, invisiblePostIds);
    }

    public PostByLocationResponse getPostByLocation(Post post, Long userId) {
        String url = getImageUrl(post.getThumbnailImageId());
        Map<String, List<String>> tags = getTagsByPostId(post.getId());

        boolean like = checkLikeByPostAndUser(post.getId(), userId);

        return PostByLocationResponse.of(
            post.getId(),
            url,
            tags,
            like,
            post.getGender()
        );
    }
}