package com.WearWeather.wear.domain.tag.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public void saveTags(Post post, PostCreateRequest request) {
        saveTags(post, request.getWeatherTagIds());
        saveTags(post, request.getTemperatureTagIds());
        saveTag(post, request.getSeasonTagId());
    }

    private void saveTags(Post post, Set<Long> tagIds) {
        for (Long tagId : tagIds) {
            saveTag(post, tagId);
        }
    }

    private void saveTag(Post post, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
        PostTag postTag = PostTag.builder()
            .post(post)
            .tag(tag)
            .build();
        postTagRepository.save(postTag);
        post.addPostTag(postTag);
    }
}
