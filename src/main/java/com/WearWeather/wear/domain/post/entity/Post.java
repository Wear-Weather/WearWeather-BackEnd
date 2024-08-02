package com.WearWeather.wear.domain.post.entity;

import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.*;
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
public class Post extends BaseTimeEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Embedded
    @JoinColumn(unique = true)
    private Location location;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Column(name = "likeCount", nullable = false)
    private int likeCount = 0;

    @Column(name = "isDelete", nullable = false)
    private boolean isDelete = false;

    public void updateLikeCount(){
        this.likeCount ++;
    }
}
