package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostByTemperatureRepositoryCustom {
    Page<Post> findPostsByTmp(int rangeStart, int rangeEnd, Pageable pageable, List<Long> invisiblePostIds);

}
