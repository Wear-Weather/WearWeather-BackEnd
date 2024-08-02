package com.WearWeather.wear.domain.post.service;

import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.repository.PostRepository;
import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postImage.repository.PostImageRepository;
import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.domain.tag.repository.TagRepository;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostImageRepository postImageRepository;
    private final UserService userService;

    public Long createPost(String email, PostCreateRequest request) {
        User user = userService.getUserByEmail(email);
        Post post = request.toEntity(user.getUserId());

        addImagesToPost(request, post);
        postRepository.save(post);
        saveAllTags(post, request.getTagsMap());

        return post.getPostId();
    }

    private void addImagesToPost(PostCreateRequest request, Post post) {
        List<PostImage> postImages = postImageRepository.findByIdIn(request.getImageId());

        for (PostImage postImage : postImages) {
            if (postImage.getPost() != null) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
            }
            post.addPostImages(postImage);
        }
    }

    private void saveAllTags(Post post, Map<String, Set<String>> tagsMap) {
        for (Map.Entry<String, Set<String>> entry : tagsMap.entrySet()) {
            saveTags(post, entry.getKey(), entry.getValue());
        }
    }

    private void saveTags(Post post, String category, Set<String> tags) {
        for (String tag : tags) {
            saveTag(post, category, tag);
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
}