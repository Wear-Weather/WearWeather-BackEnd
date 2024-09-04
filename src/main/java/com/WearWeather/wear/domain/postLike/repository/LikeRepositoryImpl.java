package com.WearWeather.wear.domain.postLike.repository;

import com.WearWeather.wear.domain.postLike.entity.QLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

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
    public List<Long> findByUserId(Long userId, Pageable pageable) {
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
}
