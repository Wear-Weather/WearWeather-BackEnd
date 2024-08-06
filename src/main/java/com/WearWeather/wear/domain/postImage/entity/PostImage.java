package com.WearWeather.wear.domain.postImage.entity;


import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String name;         // s3에 저장된 이름

    @Column(nullable = false)
    private String originName;   // 원래 이름

    private int byteSize;

    private int width;

    private int height;

    @Builder
    public PostImage(Post post, String name, String originName, int byteSize, int width, int height) {
        this.post = post;
        this.name = name;
        this.originName = originName;
        this.byteSize = byteSize;
        this.width = width;
        this.height = height;
    }

    public void addPost(Post post) {
        this.post = post;
    }
}
