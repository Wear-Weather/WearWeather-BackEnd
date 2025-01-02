package com.WearWeather.wear.domain.postLike.facade;

import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostByMeResponse;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.repository.LikeRepository;
import com.WearWeather.wear.domain.postLike.service.LikeService;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.domain.user.service.UserService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LikeReaderFacade {

    private final PostService postService;
    private final PostReportService postReportService;
    private final PostHiddenService postHiddenService;
    private final LikeService likeService;
    private final PostImageService postImageService;
    private final PostTagService postTagService;
    private final LocationService locationService;

    public LikedPostsByMeResponse getLikedPostsByMe(Long userId, int page, int size) {
        List<Long> invisiblePostIdsList = getInvisiblePostIdsList(userId);

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Long> likedPostIds = likeService.getByUserIdNotInHiddenPosts(userId, pageable, invisiblePostIdsList);

        List<LikedPostByMeResponse> likedPosts = getLikedPostsByMe(userId, likedPostIds);
        int totalPage = likedPostIds.getTotalPages() -1 ;

        return LikedPostsByMeResponse.of(likedPosts, totalPage);
    }

    public List<Long> getInvisiblePostIdsList(Long userId) {
        List<Long> hiddenPostIds = postHiddenService.getHiddenPostsByUserId(userId);
        List<Long> reportedByMePostIds = postReportService.getReportedPostsByUserId(userId);
        List<Long> reportedPostIds = postReportService.getPostsExceedingReportCount();

        return postService.getDistinctMergedPostIdsList(hiddenPostIds, reportedByMePostIds, reportedPostIds);
    }

    public List<LikedPostByMeResponse> getLikedPostsByMe(Long userId, Page<Long> likedPostIds) {
        List<Post> posts = postService.getPagePosts(likedPostIds);

        return posts.stream()
          .map(post -> getLikedPost(userId, post))
          .toList();
    }

    public LikedPostByMeResponse getLikedPost(Long userId, Post post) {
        String url = postImageService.getImageUrl(post.getThumbnailImageId());
        LocationResponse location = locationService.getCityAndDistrict(post.getLocation().getCity(), post.getLocation().getDistrict());

        Map<String, List<String>> tags = postTagService.getTagsByPostId(post.getId());
        boolean like = likeService.checkLikeByPostAndUser(post.getId(), userId);

        return LikedPostByMeResponse.of(
          post.getId(),
          url,
          location,
          tags,
          like
        );
    }

}
