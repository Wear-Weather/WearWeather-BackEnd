package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.entity.Post;

import java.util.List;


public interface PostRepositoryCustom {
        List<Post> findPostsByFilters(PostsByFiltersRequest request);
}
