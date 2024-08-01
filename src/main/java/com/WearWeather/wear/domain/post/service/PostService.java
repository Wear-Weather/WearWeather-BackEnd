package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public Long createPost(String email, PostCreateRequest request) {
        User user = findByUserEmail(email);
        Post post = request.toEntity(user.getUserId());

        postRepository.save(post);

        saveTags(post, "weather", request.getWeatherTags());
        saveTags(post, "temperature", request.getTemperatureTags());
        saveTag(post, "season", request.getSeason().name());

        return post.getPostId();
    }

    private void saveTags(Post post, String category, Set<? extends Enum<?>> tags) {
        for (Enum<?> tagEnum : tags) {
            saveTag(post, category, tagEnum.name());
        }
    }

    private void saveTag(Post post, String category, String content) {
        Tag tag = Tag.builder()
            .post(post)
            .category(category)
            .content(content)
            .build();
        tagRepository.save(tag);
        post.addTag(tag);
    }

    private User findByUserEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));
    }
}
