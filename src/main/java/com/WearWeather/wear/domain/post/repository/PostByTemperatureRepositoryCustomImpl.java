package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.QPost;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
@RequiredArgsConstructor
public class PostByTemperatureRepositoryCustomImpl implements PostByTemperatureRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QPost qPost = QPost.post;

    @Override
    public Page<Post> findPostsByTmp(String tmp, Pageable pageable, List<Long> invisiblePostIds) {

        List<Post> posts = fetchPosts(pageable, tmp, invisiblePostIds);
        JPAQuery<Long> postsQueryCount = getPostsQueryCount(tmp, invisiblePostIds);

        return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);
    }

    private List<Post> fetchPosts(Pageable pageable, String tmp, List<Long> invisiblePostIds){
        return jpaQueryFactory
                .selectFrom(qPost)
                .where(
                        qPost.temperature.eq(tmp),
                        qPost.id.notIn(invisiblePostIds)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qPost.createdAt.desc())
                .fetch();
    }

    private JPAQuery<Long> getPostsQueryCount(String tmp, List<Long> invisiblePostIds){

        return jpaQueryFactory
                .select(qPost.count())
                .from(qPost)
                .where(
                    qPost.temperature.eq(tmp),
                    qPost.id.notIn(invisiblePostIds)
                );
    }
}