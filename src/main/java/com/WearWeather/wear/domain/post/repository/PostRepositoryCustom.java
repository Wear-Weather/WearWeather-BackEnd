package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostWithLocationName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
        Page<PostWithLocationName> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable);
}
