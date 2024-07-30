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
public class Post extends BaseTimeEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "likeCount", nullable = false)
    private int likeCount;

    @Column(name = "isDelete", nullable = false)
    private boolean isDelete;

}
