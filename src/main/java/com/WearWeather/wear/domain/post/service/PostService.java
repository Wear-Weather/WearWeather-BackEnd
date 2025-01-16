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
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.SortType;
import com.WearWeather.wear.domain.postLike.service.LikeService;
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

    public Post save(Long userId, Location location, PostCreateRequest request) {
        Post post = request.toEntity(userId, location);
        postRepository.save(post);
        return post;
    }

    public void updatePostDetails(Post post, PostUpdateRequest request, Location location) {
        post.updatePostDetails(request.getTitle(), request.getContent(), request.getGender(), location);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
          .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST));
    }

    public Post validateUserPermission(Long userId, Long postId) {
        Post post = getPost(postId);
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
        Post post = getPost(postId);
        return post.updateLikeCount();
    }

    @Transactional
    public Integer removeLikeCount(Long postId) {
        Post post = getPost(postId);
        return post.removeLikeCount();
    }

    public List<Post> getPostsByIds(List<Long> postIds) {
        List<Post> posts = postRepository.findAllByIdIn(postIds);

        return postIds.stream()
          .map(postId -> posts.stream()
            .filter(post -> post.getId().equals(postId))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_POST)))
          .toList();
    }

    public List<Long> getDistinctMergedPostIdsList(List<Long> hiddenPostIds, List<Long> reportedByMePostIds, List<Long> reportedPostIds) {
        Set<Long> postIdsSet = new LinkedHashSet<>(hiddenPostIds);
        postIdsSet.addAll(reportedByMePostIds);
        postIdsSet.addAll(reportedPostIds);
        return new ArrayList<>(postIdsSet);
    }

    public Page<Post> getPostsExcludingInvisiblePosts(Pageable pageable, Location location, List<Long> invisiblePostIds) {
        return postRepository.getPostsExcludingInvisiblePosts(pageable, location, invisiblePostIds);
    }

    public Page<PostWithLocationName> getPostByFiltersNEWWWWW(PostsByFiltersRequest request, Pageable pageable, List<Long> invisiblePostIds) {
        return postRepository.findPostsByFilters(request, pageable, invisiblePostIds);
    }

    public Page<Post> getPostByTemperatureNEWWW(int rangeStart, int rangeEnd, Pageable pageable, List<Long> invisiblePostIds) {
        return postRepository.findPostsByTmp(rangeStart, rangeEnd, pageable, invisiblePostIds);
    }

    public Page<Post> getPostByUserId(Long userId, Pageable pageable) {
       return postRepository.findByUserId(userId, pageable);
    }

    public List<Post> getPagePosts(Page<Long> likedPostIds){
        return postRepository.findAllById(likedPostIds);
    }
}
