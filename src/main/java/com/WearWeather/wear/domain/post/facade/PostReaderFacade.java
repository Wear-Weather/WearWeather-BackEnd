package com.WearWeather.wear.domain.post.facade;

import com.WearWeather.wear.domain.location.service.LocationService;
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
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.domain.weather.domain.OutfitGuideByTemperature;
import java.util.Collections;
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

@Service
@RequiredArgsConstructor
public class PostReaderFacade {

    private final PostTagService postTagService;
    private final UserService userService;
    private final LocationService locationService;
    private final PostReportService postReportService;
    private final PostHiddenService postHiddenService;
    private final PostService postService;
    private final PostImageService postImageService;
    private final LikeService likeService;

    private static final String SORT_COLUMN_BY_CREATE_AT = "createdAt";
    private static final String SORT_COLUMN_BY_LIKE_COUNT = "likeCount";

    /**
     * getTopLikedPosts
     **/
    public List<TopLikedPostResponse> getTopLikedPosts(Long userId) {
        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();
        List<Long> filteredPostIds = likeService.getMostLikedPostIdForDay(invisiblePostIds);
        List<Post> posts = postService.getPostsByIds(filteredPostIds);

        return posts.stream()
          .map(post -> getTopLikedPost(post, userId))
          .collect(Collectors.toList());
    }

    /**
     * getPostDetail
     **/
    public PostDetailResponse getPostDetail(Long userId, Long postId) {
        Post post = postService.getPost(postId);

        String postUserNickname = userService.getNicknameById(post.getUserId());
        ImagesResponse imageUrlList = getImagesResponse(post.getId());
        LocationResponse location = locationService.getCityAndDistrict(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());

        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);
        boolean report = postReportService.hasReports(post.getId());

        return PostDetailResponse.of(
          postUserNickname,
          post,
          imageUrlList,
          location,
          tags,
          like,
          report);
    }

    /**
     * getPostsByLocation
     **/
    public PostsByLocationResponse getPostsByLocation(Long userId, int page, int size, String city, String district, SortType sort) {
        Location location = locationService.getCityAndDistrict(city, district);
        Page<Post> posts = getPostByLocation(page, size, location, sort, userId);

        List<PostByLocationResponse> responses = posts.stream()
          .map(post -> getPostByLocation(post, userId))
          .toList();
        int totalPages = posts.getTotalPages() - 1;

        return PostsByLocationResponse.of(LocationResponse.of(city, district), responses, totalPages);
    }


    /**
     * getPosts
     */
    public PostsByFiltersResponse getPosts(Long userId, PostsByFiltersRequest request) {
        Page<PostWithLocationName> posts = getPostByFilters(request, userId);

        List<SearchPostResponse> responses = posts.stream()
          .map(post -> getPostByFilters(post, userId))
          .toList();
        int totalPage = posts.getTotalPages() - 1;

        return PostsByFiltersResponse.of(responses, totalPage);
    }

    /**
     * getPostsByTemperature
     */
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

    /**
     * getPostsByMe
     */
    public PostsByMeResponse getPostsByMe(Long userId, int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postService.getPostByUserId(userId, pageable);

        List<PostByMeResponse> postByMe = posts.stream()
          .map(post -> getPostByMe(userId, post))
          .toList();

        int totalPage = posts.getTotalPages() - 1;
        return PostsByMeResponse.of(postByMe, totalPage);
    }


    public List<Long> getInvisiblePostIdsList(Long userId) {
        List<Long> hiddenPostIds = postHiddenService.getHiddenPostsByUserId(userId);
        List<Long> reportedByMePostIds = postReportService.getReportedPostsByUserId(userId);
        List<Long> reportedPostIds = postReportService.getPostsExceedingReportCount();

        return postService.getDistinctMergedPostIdsList(hiddenPostIds, reportedByMePostIds, reportedPostIds);
    }

    public TopLikedPostResponse getTopLikedPost(Post post, Long userId) {
        String url = postImageService.getImageUrl(post.getThumbnailImageId());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());
        LocationResponse location = locationService.getCityAndDistrict(post.getLocation().getCity(), post.getLocation().getDistrict());
        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);

        return TopLikedPostResponse.of(
          post,
          url,
          location,
          tags,
          like);
    }


    public ImagesResponse getImagesResponse(Long postId) {
        return ImagesResponse.of(getImageDetailResponseList(postId));
    }

    public List<ImageDetailResponse> getImageDetailResponseList(Long postId) {
        List<PostImage> postImages = postImageService.getPostImagesByPost(postId);

        return postImages.stream()
          .map(image -> ImageDetailResponse.of(image.getId(), postImageService.getImageUrl(image.getId())))
          .toList();
    }

    public Page<Post> getPostByLocation(int page, int size, Location location, SortType sort, Long userId) {
        String sortType = getSortColumnName(sort);
        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortType).descending());

        return postService.getPostsExcludingInvisiblePosts(pageable, location, invisiblePostIds);
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
        String url = postImageService.getImageUrl(post.getThumbnailImageId());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());
        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);

        return PostByLocationResponse.of(
          post.getId(),
          url,
          tags,
          like
        );
    }


    public Page<PostWithLocationName> getPostByFilters(PostsByFiltersRequest request, Long userId) {
        String sortType = getSortColumnName(request.sort());
        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();
        Pageable pageable = PageRequest.of(request.page(), request.size(), Sort.by(sortType).descending());

        return postService.getPostByFiltersNEWWWWW(request, pageable, invisiblePostIds);
    }

    public SearchPostResponse getPostByFilters(PostWithLocationName post, Long userId) {
        String url = postImageService.getImageUrl(post.thumbnailImageId());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.postId());
        boolean like = likeService.checkLikeByPostAndUser(post.postId(), userId);

        return SearchPostResponse.of(
          post,
          url,
          tags,
          like,
          post.gender()
        );
    }


    public Page<Post> getPostByTemperature(Long userId, int rangeStart, int rangeEnd, int page, int size) {
        List<Long> invisiblePostIds = (userId != null) ? getInvisiblePostIdsList(userId) : Collections.emptyList();
        Pageable pageable = PageRequest.of(page, size);
        return postService.getPostByTemperatureNEWWW(rangeStart, rangeEnd, pageable, invisiblePostIds);
    }

    public PostByTemperatureResponse getPostByTemperature(Post post, Long userId) {
        String url = postImageService.getImageUrl(post.getThumbnailImageId());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());
        LocationResponse location = locationService.getCityAndDistrict(post.getLocation().getCity(), post.getLocation().getDistrict());
        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);

        return PostByTemperatureResponse.of(
          post.getId(),
          url,
          location,
          tags,
          like
        );
    }


    private PostByMeResponse getPostByMe(Long userId, Post post) {
        String url = postImageService.getImageUrl(post.getThumbnailImageId());
        LocationResponse location = locationService.getCityAndDistrict(post.getLocation().getCity(), post.getLocation().getDistrict());
        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());

        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);
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

}
