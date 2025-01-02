package com.WearWeather.wear.domain.post.facade;

import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateFacade {

    private final PostService postService;
    private final PostTagService postTagService;
    private final LocationService locationService;
    private final PostImageService postImageService;

    @Transactional
    public void updatePost(Long userId, Long postId, PostUpdateRequest request) {
        Post post = validateUserPermission(userId, postId);
        Location location = locationService.getCityAndDistrict(request.getCity(),request.getDistrict());
        update(post, request, location);
    }

    public Post validateUserPermission(Long userId, Long postId) {
        Post post = postService.getPost(postId);
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return post;
    }

    public void update(Post post, PostUpdateRequest request, Location location){
        postService.updatePostDetails(post, request, location);
        postImageService.updatePostImages(post, request);
        postTagService.updatePostTags(post,request);
    }

}
