package com.WearWeather.wear.domain.postTag.service;

import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostTagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    @Transactional
    public void saveAllTag(Long postId, TaggableRequest request) {
        saveTags(postId, request.getWeatherTagIds());
        saveTags(postId, request.getTemperatureTagIds());
        saveTag(postId, request.getSeasonTagId());
    }

    @Transactional
    private void saveTags(Long postId, Set<Long> tagIds) {
        for (Long tagId : tagIds) {
            saveTag(postId, tagId);
        }
    }

    @Transactional
    private void saveTag(Long postId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
        PostTag postTag = PostTag.builder()
            .postId(postId)
            .tagId(tagId)
            .build();
        postTagRepository.save(postTag);
    }

    @Transactional
    public void deleteTagsByPost(Long postId) {
        postTagRepository.deleteByPostId(postId);
    }

    public List<PostTag> findPostTagsByPostId(Long postId){
         return postTagRepository.findByPostId(postId);
    }


}
