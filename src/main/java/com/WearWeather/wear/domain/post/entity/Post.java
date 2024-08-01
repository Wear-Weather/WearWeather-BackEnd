package com.WearWeather.wear.domain.post.entity;


import com.WearWeather.wear.domain.tag.entity.Tag;
import com.WearWeather.wear.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Setter
@Table(name = "post")
public class Post extends BaseTimeEntity implements Serializable {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "content", length = 300, nullable = false)
    private String content;

    @Embedded
    private Location location;

    @Column(name = "likeCount", nullable = false)
    private int likeCount;

    @Column(name = "isDelete", nullable = false)
    private boolean isDelete;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.setPost(this);
    }
}
