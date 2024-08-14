package com.WearWeather.wear.domain.postImage.repository;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);

    List<PostImage> findByIdIn(List<Long> ids);
}
