package com.WearWeather.wear.domain.postReport.service;

import com.WearWeather.wear.domain.post.service.PostValidationService;
import com.WearWeather.wear.domain.postReport.repository.PostIdMapping;
import com.WearWeather.wear.domain.postReport.entity.PostReport;
import com.WearWeather.wear.domain.postReport.repository.PostReportRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReportService {

    private final PostValidationService postValidationService;
    private final PostReportRepository postReportRepository;

    @Transactional
    public void reportPost(Long userId, Long postId, String reason) {
        postValidationService.validatePostExists(postId);

        if (postReportRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new CustomException(ErrorCode.REPORT_POST_ALREADY_EXIST);
        }

        PostReport postReport = PostReport.builder()
            .userId(userId)
            .postId(postId)
            .reason(reason)
            .build();
        postReportRepository.save(postReport);
    }

    public boolean hasExceededReportCount(Long postId){
        int reportCount = 5;
        return findReportPost(postId) >= reportCount;
    }
    public Long findReportPost(Long postId){
        return postReportRepository.countByPostId(postId);
    }

    public boolean checkPostReported(Long postId){
        return postReportRepository.existsByPostId(postId);
    }

    public boolean hasReports(Long postId){
        return checkPostReported(postId) && hasExceededReportCount(postId);
    }

    public List<Long> getPostsExceedingReportCount(){
        return postReportRepository.findPostsExceedingReportCount();
    }

    @Transactional
    public void deleteReportByPostId(Long postId) {
        postReportRepository.deleteByPostId(postId);
    }

    public List<Long> getReportedPostsByUserId(Long userId){
        List<PostIdMapping> postIdMappings = postReportRepository.findAllByUserId(userId);

        return postIdMappings.stream()
            .map(PostIdMapping::getPostId)
            .toList();
    }
}
