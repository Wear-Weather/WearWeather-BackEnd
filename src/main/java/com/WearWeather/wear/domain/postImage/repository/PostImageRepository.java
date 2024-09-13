package com.WearWeather.wear.domain.postImage.repository;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);

    List<PostImage> findByIdIn(List<Long> ids);

    @Query("SELECT pi.id FROM PostImage pi WHERE pi.postId = :postId")
    List<Long> findImageIdsByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);}
