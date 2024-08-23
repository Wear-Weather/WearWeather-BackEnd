package com.WearWeather.wear.domain.postLike.repository;

import com.WearWeather.wear.domain.postLike.entity.QLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findMostLikedPostIdForDay() {
        QLike qLike = QLike.like;

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return jpaQueryFactory
                .select(qLike.postId)
                .from(qLike)
                .where(
                        qLike.createdAt.between(startOfDay, endOfDay)
                )
                .groupBy(qLike.postId)
                .orderBy(qLike.count().desc())
                .limit(10)
                .fetch();
    }
}
