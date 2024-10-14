package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostByLocationRepositoryCustomImpl implements PostByLocationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QPost qPost = QPost.post;

    @Override
    public Page<Post> getPostsNotInHiddenPosts(Pageable pageable, Location location, List<Long> notInPostIds) {

        List<Post> posts = fetchPosts(pageable, location, notInPostIds);
        JPAQuery<Long> postsQueryCount = getPostsQueryCount(location, notInPostIds);

        return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);
    }

    private List<Post> fetchPosts(Pageable pageable, Location location, List<Long> notInPostIds){
        return jpaQueryFactory
                .selectFrom(qPost)
                .where(
                        qPost.location.eq(location),
                        qPost.id.notIn(notInPostIds)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortColumn(pageable.getSort()))
                .fetch();
    }
    private OrderSpecifier<?> getSortColumn(Sort sort){

        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, qPost.createdAt);
        }

        Sort.Order order = sort.iterator().next();
        String sortColumn = order.getProperty();
        Order direction = order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;

        return switch (sortColumn) {
            case "createdAt" -> new OrderSpecifier<>(direction, qPost.createdAt);
            case "likeCount" -> new OrderSpecifier<>(direction, qPost.likeCount);
            default -> new OrderSpecifier<>(Order.DESC, qPost.createdAt);
        };
    }

    private JPAQuery<Long> getPostsQueryCount(Location location, List<Long> notInPostIds){

        return jpaQueryFactory
                .select(qPost.count())
                .from(qPost)
                .where(
                        qPost.location.eq(location),
                        qPost.id.notIn(notInPostIds)
                );
    }
}