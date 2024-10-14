package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostByLocationRepositoryCustom {
    Page<Post> getPostsExcludingInvisiblePosts(Pageable pageable, Location location, List<Long> invisiblePostIdsList);
}
