package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostRepositoryCustom {
        Page<Post> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable);
}
