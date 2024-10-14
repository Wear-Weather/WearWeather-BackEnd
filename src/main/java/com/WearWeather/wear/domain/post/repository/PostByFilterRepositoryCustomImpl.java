package com.WearWeather.wear.domain.post.repository;

import com.WearWeather.wear.domain.location.entity.QCity;
import com.WearWeather.wear.domain.location.entity.QDistrict;
import com.WearWeather.wear.domain.post.dto.request.LocationRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostWithLocationName;
import com.WearWeather.wear.domain.post.entity.Location;
import com.WearWeather.wear.domain.post.entity.QPost;
import com.WearWeather.wear.domain.postTag.entity.QPostTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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
public class PostByFilterRepositoryCustomImpl implements PostByFilterRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QPost qPost = QPost.post;
    QPostTag qPostTag = QPostTag.postTag;
    QCity qCity = QCity.city1;
    QDistrict qDistrict = QDistrict.district1;

    BooleanBuilder tagConditions = new BooleanBuilder();
    BooleanBuilder havingConditions = new BooleanBuilder();

    @Override
    public Page<PostWithLocationName> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable, List<Long> invisiblePostIdsList) {

        List<Long> postIdsByLocation = findPostIdByLocationFilter(request);
        List<Long> postIdsByTag = findPostIdByTagFilter(request);

        List<PostWithLocationName> posts = getQueryByFilters(postIdsByLocation, postIdsByTag, pageable, invisiblePostIdsList);
        JPAQuery<Long> postsQueryCount = getPostsQueryCount(postIdsByLocation, postIdsByTag, invisiblePostIdsList);

        return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);

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

        List<Location> locationList = request.getLocationList().stream()
                .map(LocationRequest::toEntity)
                .toList();

        JPAQuery<Long> postIdByLocationFilter = jpaQueryFactory
                .select(qPost.id)
                .from(qPost);

        if (!locationList.isEmpty()) {

            boolean hasCityEntireValue = checkSearchAllCity(locationList);

            if (!hasCityEntireValue) {
                BooleanExpression locationTagCondition = checkSearchAllDistrictInCity(locationList);

                postIdByLocationFilter.where(locationTagCondition);
            }
        }
        return postIdByLocationFilter.fetch();
    }

    private boolean checkSearchAllCity(List<Location> locationList){
        List<Location> allCity = List.of(new Location(findAllCityId(),0L));
        return locationList.equals(allCity);
    }

    private Long findAllCityId(){

        return jpaQueryFactory
                .select(qCity.id)
                .from(qCity)
                .where(qCity.city.eq("전국"))
                .fetchOne();
    }

    public BooleanExpression createTagCondition(QPostTag postTag, List<Long> tagIds){

        if(tagIds == null || tagIds.isEmpty()){
            return null;
        }

        return postTag.tagId.in(tagIds);
    }

    private BooleanExpression checkSearchAllDistrictInCity(List<Location> locationList){
        Long districtEntireValue = 0L;

        boolean hasDistrictEntireValue = locationList.stream()
                .anyMatch(location -> location.getDistrict().equals(districtEntireValue));

        if (hasDistrictEntireValue) {
            List<Long> city = extractCityListForEntireDistrict(locationList, districtEntireValue);
            List<Location> otherCityList = extractCityListForOtherDistrict(locationList, districtEntireValue);

            if(!otherCityList.isEmpty()){
                return qPost.location.city.in(city)
                        .or(qPost.location.in(otherCityList));
            }

            return qPost.location.city.in(city);

        }
        return qPost.location.in(locationList);
    }

    private List<Long> extractCityListForEntireDistrict(List<Location> locationList, Long districtEntireValue){

        return locationList.stream()
                .filter(location -> location.getDistrict().equals(districtEntireValue))
                .map(Location::getCity)
                .distinct()
                .toList();
    }

    private List<Location> extractCityListForOtherDistrict(List<Location> locationList, Long districtEntireValue){
        return locationList.stream()
                .filter(location -> !location.getDistrict().equals(districtEntireValue))
                .toList();
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
            return new OrderSpecifier<>(Order.DESC, qPost.createdAt); //기본 정렬
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

    private List<PostWithLocationName> getQueryByFilters(List<Long> postIdsByLocation, List<Long> postIdsByTag, Pageable pageable, List<Long> invisiblePostIdsList){

        OrderSpecifier<?> sortType = getSortColumn(pageable.getSort());

        return jpaQueryFactory
                .select(Projections.constructor(PostWithLocationName.class,
                        qPost.id.as("postId"),
                        qPost.thumbnailImageId.as("thumbnailImageId"),
                        qCity.city.as("cityName"),
                        qDistrict.district.as("districtName")
                ))
                .from(qPost)
                .leftJoin(qCity).on(qPost.location.city.eq(qCity.id))
                .leftJoin(qDistrict).on(qPost.location.district.eq(qDistrict.id))
                .where(
                        qPost.id.in(postIdsByLocation),
                        qPost.id.in(postIdsByTag),
                        qPost.id.notIn(invisiblePostIdsList)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortType)
                .fetch();
    }

    private JPAQuery<Long> getPostsQueryCount(List<Long> postIdsByLocation, List<Long> postIdsByTag, List<Long> invisiblePostIdsList){

        return jpaQueryFactory
                .select(qPost.count())
                .from(qPost)
                .where(
                        qPost.id.in(postIdsByLocation),
                        qPost.id.in(postIdsByTag),
                        qPost.id.notIn(invisiblePostIdsList)
                );
    }
}
