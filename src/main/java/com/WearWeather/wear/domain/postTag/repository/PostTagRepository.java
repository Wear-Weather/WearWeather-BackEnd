package com.WearWeather.wear.domain.postTag.repository;

import com.WearWeather.wear.domain.postTag.entity.PostTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}
