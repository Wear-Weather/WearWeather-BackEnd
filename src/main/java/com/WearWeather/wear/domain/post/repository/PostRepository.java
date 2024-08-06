package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
