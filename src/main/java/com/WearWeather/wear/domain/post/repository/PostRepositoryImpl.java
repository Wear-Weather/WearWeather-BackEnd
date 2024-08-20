package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.QPost;
import com.WearWeather.wear.domain.postTag.entity.QPostTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QPost qPost = QPost.post;
    QPostTag qPostTag = QPostTag.postTag;

    BooleanBuilder tagConditions = new BooleanBuilder();
    BooleanBuilder havingConditions = new BooleanBuilder();

    @Override
    public Page<Post> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable) {

        List<Long> postIdsByLocation = findPostIdByLocationFilter(request);
        List<Long> postIdsByTag = findPostIdByTagFilter(request);

        List<Post> postsByPageable = getQueryByFilters(postIdsByLocation, postIdsByTag, pageable);
        JPAQuery<Long> postsQueryCount = getPostsQueryCount(postIdsByLocation, postIdsByTag);

        return PageableExecutionUtils.getPage(postsByPageable, pageable, postsQueryCount::fetchOne);

    }

    public List<Long> findPostIdByTagFilter(PostsByFiltersRequest request){

        List<Long> seasonTagIds = request.getSeasonTagIds();
        List<Long> weatherTagIds = request.getWeatherTagIds();
        List<Long> temperatureTagIds = request.getTemperatureTagIds();

        JPAQuery<Long> postIdByTagFilter = jpaQueryFactory.select(qPostTag.postId)
                .from(qPostTag)
                .groupBy(qPostTag.postId);

        BooleanExpression seasonTagCondition = createTagCondition(qPostTag, seasonTagIds);
        createWhereAndHavingCondition(seasonTagCondition, seasonTagIds);

        BooleanExpression weatherTagCondition = createTagCondition(qPostTag, weatherTagIds);
        createWhereAndHavingCondition(weatherTagCondition, weatherTagIds);

        BooleanExpression temperatureTagCondition = createTagCondition(qPostTag, temperatureTagIds);
        createWhereAndHavingCondition(temperatureTagCondition, temperatureTagIds);

        if(tagConditions.hasValue()){
            postIdByTagFilter.where(tagConditions);
        }

        if(havingConditions.hasValue()){
            postIdByTagFilter.having(havingConditions);
        }

        return postIdByTagFilter.fetch();
    }

    public List<Long> findPostIdByLocationFilter(PostsByFiltersRequest request) {

        List<Location> locationList = request.getLocationList();

        JPAQuery<Long> postIdByLocationFilter = jpaQueryFactory
                .select(qPost.id)
                .from(qPost);

        List<Location> allCity = List.of(new Location(1L,0L));

        if (locationList != null && !locationList.isEmpty()) {
            BooleanExpression locationTagCondition;

            if (!locationList.equals(allCity)) {
                locationTagCondition = qPost.location.in(locationList);
                postIdByLocationFilter.where(locationTagCondition);
            }
        }

        return postIdByLocationFilter.fetch();
    }

        public BooleanExpression createTagCondition(QPostTag postTag, List<Long> tagIds){

        if(tagIds == null || tagIds.isEmpty()){
            return null;
        }

        return postTag.tagId.in(tagIds);
    }

    public BooleanExpression havingCondition(NumberPath<Long> tagId, List<Long> tagIds){
        return Expressions.numberTemplate(Long.class,
                "SUM(CASE WHEN {0} IN {1} THEN 1 ELSE 0 END)", tagId, tagIds).gt(0);
    }

    public void createWhereAndHavingCondition(BooleanExpression tagCondition, List<Long> tagIds){
        if(tagCondition != null){
            tagConditions.or(tagCondition);
            havingConditions.and(havingCondition(qPostTag.tagId, tagIds));
        }
    }

    private OrderSpecifier<?> getSortColumn(Sort sort){

        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier<>(Order.DESC, qPost.createAt); //기본 정렬
        }

        Sort.Order order = sort.iterator().next();
        String sortColumn = order.getProperty();
        Order direction = order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;

        return switch (sortColumn) {
            case "createAt" -> new OrderSpecifier<>(direction, qPost.createAt);
            case "likeCount" -> new OrderSpecifier<>(direction, qPost.likeCount);
            default -> new OrderSpecifier<>(Order.DESC, qPost.createAt);
        };
    }

    private List<Post> getQueryByFilters(List<Long> postIdsByLocation, List<Long> postIdsByTag, Pageable pageable){

        OrderSpecifier<?> sortType = getSortColumn(pageable.getSort());

        return jpaQueryFactory
                .selectFrom(qPost)
                .where(
                        qPost.id.in(postIdsByLocation),
                        qPost.id.in(postIdsByTag)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortType)
                .fetch();
    }

    private JPAQuery<Long> getPostsQueryCount(List<Long> postIdsByLocation, List<Long> postIdsByTag){

        return jpaQueryFactory
                .select(qPost.count())
                .from(qPost)
                .where(
                        qPost.id.in(postIdsByLocation),
                        qPost.id.in(postIdsByTag)
                );
    }
}
