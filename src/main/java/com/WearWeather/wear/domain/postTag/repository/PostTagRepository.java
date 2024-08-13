package com.WearWeather.wear.domain.postTag.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    void deleteByPost(Post post);
}
