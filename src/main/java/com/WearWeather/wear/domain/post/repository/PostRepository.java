package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostByFilterRepositoryCustom {

    List<Post> findAllByIdIn(List<Long> postIdList);
    Page<Post> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT id FROM Post ORDER BY id DESC LIMIT 20")
    List<Long> find20IdsByOrderByIdDesc();
}
