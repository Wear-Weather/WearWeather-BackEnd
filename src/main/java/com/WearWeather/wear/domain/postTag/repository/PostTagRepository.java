package com.WearWeather.wear.domain.postTag.repository;

import com.WearWeather.wear.domain.postTag.entity.PostTag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);}
