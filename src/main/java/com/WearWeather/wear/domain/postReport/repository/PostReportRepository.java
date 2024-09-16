package com.WearWeather.wear.domain.postReport.repository;

import com.WearWeather.wear.domain.postReport.entity.PostReport;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Long countByPostId(long postId);

    boolean existsByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostReport pr WHERE pr.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
