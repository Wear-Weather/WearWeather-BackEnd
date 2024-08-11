package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPostIdInOrderByLikeCountDesc(List<Long> postIdList);
}
