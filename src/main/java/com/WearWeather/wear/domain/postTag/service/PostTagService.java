package com.WearWeather.wear.domain.postTag.service;

import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.domain.postTag.repository.PostTagRepository;
import com.WearWeather.wear.domain.tag.dto.TaggableRequest;
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
    public void saveTags(Post post, TaggableRequest request) {
        saveMultipleTags(post.getId(), request.getWeatherTagIds());
        saveMultipleTags(post.getId(), request.getTemperatureTagIds());
        saveTag(post.getId(), request.getSeasonTagId());
    }

    @Transactional
    private void saveMultipleTags(Long postId, Set<Long> tagIds) {
        for (Long tagId : tagIds) {
            saveTag(postId, tagId);
        }
    }

    @Transactional
    private void saveTag(Long postId, Long tagId) {
        validateTagId(tagId);

        PostTag postTag = PostTag.builder()
            .postId(postId)
            .tagId(tagId)
            .build();
        postTagRepository.save(postTag);
    }

    private void validateTagId(Long tagId) {
        tagRepository.findById(tagId)
            .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
    }

    @Transactional
    public void updatePostTags(Post post, TaggableRequest request) {
        deleteTagsByPostId(post.getId());
        saveTags(post, request);
    }
    public List<PostTag> findPostTagsByPostId(Long postId){
         return postTagRepository.findByPostId(postId);
    }

    @Transactional
    public void deleteTagsByPostId(Long postId) {
        postTagRepository.deleteByPostId(postId);
    }


}
