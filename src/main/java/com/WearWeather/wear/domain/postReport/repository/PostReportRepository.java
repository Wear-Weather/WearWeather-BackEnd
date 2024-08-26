package com.WearWeather.wear.domain.postReport.repository;

import com.WearWeather.wear.domain.postReport.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(long postId);

    boolean existsByPostId(Long postId);
}
