package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostByLocationCustom {
    Page<Post> getPostsNotInHiddenPosts(Pageable pageable, Location location, List<Long> hiddenPosts);
}
