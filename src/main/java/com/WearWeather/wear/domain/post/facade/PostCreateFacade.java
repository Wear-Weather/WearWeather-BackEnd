package com.WearWeather.wear.domain.post.facade;


import com.WearWeather.wear.domain.location.service.LocationService;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.service.PostService;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.postTag.service.PostTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreateFacade {

    private final PostService postService;
    private final PostTagService postTagService;
    private final LocationService locationService;
    private final PostImageService postImageService;

    @Transactional
    public Long createPost(Long userId, PostCreateRequest request) {
        Location location = locationService.getCityAndDistrict(request.getCity(), request.getDistrict());
        Post post = postService.save(userId,location,request);
        postImageService.mappingPostIdInImages(post, request);
        postTagService.saveTags(post, request);
        return post.getId();
    }

}
