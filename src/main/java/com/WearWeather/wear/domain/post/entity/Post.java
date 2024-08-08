package com.WearWeather.wear.domain.post.entity;

import com.WearWeather.wear.domain.postImage.entity.PostImage;
import com.WearWeather.wear.domain.postTag.entity.PostTag;
import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "posts")
public class Post extends BaseTimeEntity implements Serializable {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Embedded
    @JoinColumn(unique = true)
    private Location location;

    @Column(name = "likeCount", nullable = false)
    private int likeCount = 0;

    @Column(name = "isDelete", nullable = false)
    private boolean isDelete = false;

    @Column(name = "thumbnail_image_id")
    private Long thumbnailImageId; // 대표 이미지 ID 필드

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @BatchSize(size = 100)
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    public void addPostImages(PostImage postImage) {
        this.postImages.add(postImage);
        postImage.addPost(this);
    }

    public void addPostTag(PostTag postTag) {
        this.postTags.add(postTag);
    }

    public void setThumbnailImageId(Long thumbnailImageId) {
        this.thumbnailImageId = thumbnailImageId;
    }

    public void updateLikeCount() {
        this.likeCount++;
    }

    public void removeLikeCount() {
        this.likeCount--;
    }
}