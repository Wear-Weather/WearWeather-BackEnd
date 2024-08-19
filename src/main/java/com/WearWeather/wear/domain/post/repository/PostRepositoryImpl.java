package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.Post;
import com.WearWeather.wear.domain.post.entity.QPost;
import com.WearWeather.wear.domain.postTag.entity.QPostTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QPost qPost = QPost.post;
    QPostTag qPostTag = QPostTag.postTag;

    BooleanBuilder tagConditions = new BooleanBuilder();
    BooleanBuilder havingConditions = new BooleanBuilder();

    @Override
    public List<Post> findPostsByFilters(PostsByFiltersRequest request) {

        //tag 필터
        List<Long> PostIdsByTag = getPostIdByTagFilter(request);

        //location 필터
        List<Long> PostIdsByLocation =getPostIdByLocationFilter(request);

        return jpaQueryFactory
                .selectFrom(qPost)
                .where(
                        qPost.id.in(PostIdsByLocation),
                        qPost.id.in(PostIdsByTag)
                )
                .orderBy(qPost.createAt.desc())
                        .fetch();

    }

    public List<Long> getPostIdByTagFilter(PostsByFiltersRequest request){

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

    public List<Long> getPostIdByLocationFilter(PostsByFiltersRequest request) {

        List<Location> locationList = request.getLocationList();

        JPAQuery<Long> postIdByLocationFilter = jpaQueryFactory
                .select(qPost.id)
                .from(qPost);

        if (locationList != null && !locationList.isEmpty()) { //TODO : 위치 데이터 Long 타입으로 변경시 수정 예정
            BooleanExpression locationTagCondition = qPost.location.in(locationList);
            postIdByLocationFilter.where(locationTagCondition);
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
}
