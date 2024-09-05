package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostWithLocationName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostByFilterRepositoryCustom {
        Page<PostWithLocationName> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable, List<Long> hiddenPostIds);
}
