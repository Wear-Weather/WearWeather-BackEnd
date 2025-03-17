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
    public Page<Post> findPostsByTmp(int rangeStart, int rangeEnd, Pageable pageable, List<Long> invisiblePostIds) {

        List<Post> posts = fetchPosts(pageable, rangeStart, rangeEnd, invisiblePostIds);
        JPAQuery<Long> postsQueryCount = getPostsQueryCount( rangeStart, rangeEnd, invisiblePostIds);

        return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);
    }

    private List<Post> fetchPosts(Pageable pageable, int rangeStart, int rangeEnd, List<Long> invisiblePostIds){
        return jpaQueryFactory
                .selectFrom(qPost)
                .where(
                        qPost.temperature.between(rangeStart, rangeEnd),
                        qPost.id.notIn(invisiblePostIds)
                )
                .limit(pageable.getPageSize())
                .orderBy(qPost.createdAt.desc())
                .fetch();
    }

    private JPAQuery<Long> getPostsQueryCount(int rangeStart, int rangeEnd, List<Long> invisiblePostIds){

        return jpaQueryFactory
                .select(qPost.count())
                .from(qPost)
                .where(
                    qPost.temperature.between(rangeStart, rangeEnd),
                    qPost.id.notIn(invisiblePostIds)
                );
    }
}