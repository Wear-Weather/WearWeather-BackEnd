package com.WearWeather.wear.domain.post.entity;

import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Embedded
    @JoinColumn(unique = true)
    private Location location;

    @Column(name = "tmp", nullable = false)
    private String temperature;

    @Column(name = "likeCount", nullable = false)
    private int likeCount = 0;

    @Column(name = "isDelete", nullable = false)
    private boolean isDelete = false;

    @Column(name = "thumbnail_image_id")
    private Long thumbnailImageId; // 대표 이미지 ID 필드

    public void addThumbnailImageId(Long postImageId) {
        this.thumbnailImageId = postImageId;
    }

    public Integer updateLikeCount() {
        this.likeCount++;
        return this.likeCount;
    }

    public Integer removeLikeCount() {
        this.likeCount--;
        return this.likeCount;
    }

    public void updatePostDetails(String title, String content, Location location) {
        this.title = title;
        this.content = content;
        this.location = location;
    }
}
