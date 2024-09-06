package com.WearWeather.wear.domain.postLike.repository;

import com.WearWeather.wear.domain.postLike.entity.QLike;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    QLike qLike = QLike.like;

    @Override
    public List<Long> findMostLikedPostIdForDay(List<Long> hiddenPostIds) {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return jpaQueryFactory
                .select(qLike.postId)
                .from(qLike)
                .where(
                        qLike.createdAt.between(startOfDay, endOfDay)
                        .and(qLike.postId.notIn(hiddenPostIds))
                )
                .groupBy(qLike.postId)
                .orderBy(
                        qLike.count().desc(),
                        qLike.postId.asc()
                )
                .limit(10)
                .fetch();
    }

    @Override
    public Page<Long> findByUserId(Long userId, Pageable pageable) {

        List<Long> posts = findLikedPostsByMeQuery(userId, pageable);
        JPAQuery<Long> postsQueryCount = getLikedPostsQueryCount(userId);

        return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);
    }

    private List<Long> findLikedPostsByMeQuery(Long userId, Pageable pageable){
        return jpaQueryFactory
                .select(qLike.postId)
                .from(qLike)
                .where(
                        qLike.userId.eq(userId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qLike.createdAt.desc())
                .fetch();
    }

    private JPAQuery<Long> getLikedPostsQueryCount(Long userId){

        return jpaQueryFactory
                .select(qLike.count())
                .from(qLike)
                .where(
                        qLike.userId.eq(userId)
                );
    }
}
