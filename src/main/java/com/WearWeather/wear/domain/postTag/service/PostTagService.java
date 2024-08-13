package com.WearWeather.wear.domain.postTag.service;

import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostTagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public void saveAllTag(Long postId, TaggableRequest request) {
        saveTags(postId, request.getWeatherTagIds());
        saveTags(postId, request.getTemperatureTagIds());
        saveTag(postId, request.getSeasonTagId());
    }

    private void saveTags(Long postId, Set<Long> tagIds) {
        for (Long tagId : tagIds) {
            saveTag(postId, tagId);
        }
    }

    private void saveTag(Long postId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
        PostTag postTag = PostTag.builder()
            .postId(postId)
            .tagId(tagId)
            .build();
        postTagRepository.save(postTag);
    }

    public void deleteTagsByPost(Long postId) {
        postTagRepository.deleteByPostId(postId);
    }
}
