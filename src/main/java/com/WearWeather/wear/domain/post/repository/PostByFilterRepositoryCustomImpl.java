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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PostByFilterRepositoryCustomImpl implements PostByFilterRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;

  QPost qPost = QPost.post;
  QPostTag qPostTag = QPostTag.postTag;
  QCity qCity = QCity.city1;
  QDistrict qDistrict = QDistrict.district1;

  @Override
  public Page<PostWithLocationName> findPostsByFilters(PostsByFiltersRequest request, Pageable pageable, List<Long> invisiblePostIdsList) {


    BooleanBuilder conditions = createConditions(request, invisiblePostIdsList);

    List<PostWithLocationName> posts = getQueryByFilters(pageable, conditions);
    JPAQuery<Long> postsQueryCount = getPostsQueryCount(conditions);

    return PageableExecutionUtils.getPage(posts, pageable, postsQueryCount::fetchOne);

  }

  private BooleanBuilder createConditions(PostsByFiltersRequest request, List<Long> invisiblePostIdsList){

    BooleanBuilder conditions = new BooleanBuilder();

    conditions.and(findPostIdByLocationFilter(request));

    List<Long> postIdsByTag = findPostIdByTagFilter(request);
    if (!postIdsByTag.isEmpty()) {
      conditions.and(qPost.id.in(postIdsByTag));
    }

    if (!invisiblePostIdsList.isEmpty()) {
      conditions.and(qPost.id.notIn(invisiblePostIdsList));
    }

    return conditions;
  }
  public List<Long> findPostIdByTagFilter(PostsByFiltersRequest request){

    BooleanBuilder tagConditions = new BooleanBuilder();
    BooleanBuilder havingConditions = new BooleanBuilder();

    List<Long> seasonTagIds = request.getSeasonTagIds();
    List<Long> weatherTagIds = request.getWeatherTagIds();
    List<Long> temperatureTagIds = request.getTemperatureTagIds();

    JPAQuery<Long> postIdByTagFilter = jpaQueryFactory.select(qPostTag.postId)
        .from(qPostTag)
        .groupBy(qPostTag.postId);

    if(!seasonTagIds.isEmpty()){
      tagConditions.or(qPostTag.tagId.in(seasonTagIds));
      havingConditions.and(havingCondition(qPostTag.tagId, seasonTagIds));
    }

    if(!weatherTagIds.isEmpty()){
      tagConditions.or(qPostTag.tagId.in(weatherTagIds));
      havingConditions.and(havingCondition(qPostTag.tagId, weatherTagIds));
    }

    if(!temperatureTagIds.isEmpty()){
      tagConditions.or(qPostTag.tagId.in(temperatureTagIds));
      havingConditions.and(havingCondition(qPostTag.tagId, temperatureTagIds));
    }

    if(tagConditions.hasValue()){
      postIdByTagFilter.where(tagConditions);
    }

    if(havingConditions.hasValue()){
      postIdByTagFilter.having(havingConditions);
    }

    return postIdByTagFilter.fetch();
  }

  public BooleanBuilder findPostIdByLocationFilter(PostsByFiltersRequest request) {

    BooleanBuilder locationConditions = new BooleanBuilder();
    List<Location> locationList = request.getLocationList().stream()
        .map(LocationRequest::toEntity)
        .toList();

    if (!locationList.isEmpty()) {
      boolean hasCityEntireValue = checkSearchAllCity(locationList);

      if (!hasCityEntireValue) {
        locationConditions.and(checkSearchAllDistrictInCity(locationList));
      }
    }

    return locationConditions;
  }

  //전국 검색
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

  private BooleanExpression checkSearchAllDistrictInCity(List<Location> locationList){
    Long districtEntireValue = 0L;

    //city 내 전체 검색
    boolean hasDistrictEntireValue = locationList.stream()
        .anyMatch(location -> location.getDistrict().equals(districtEntireValue));

    if (hasDistrictEntireValue) {
      List<Long> city = extractCityListForEntireDistrict(locationList, districtEntireValue);
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

  public BooleanExpression havingCondition(NumberPath<Long> tagId, List<Long> tagIds){
    return Expressions.numberTemplate(Long.class,
        "SUM(CASE WHEN {0} IN {1} THEN 1 ELSE 0 END)", tagId, tagIds).gt(0);
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

  private List<PostWithLocationName> getQueryByFilters(Pageable pageable, BooleanBuilder conditions){

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
          conditions
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(sortType)
        .fetch();
  }

  private JPAQuery<Long> getPostsQueryCount(BooleanBuilder conditions){

    return jpaQueryFactory
        .select(qPost.count())
        .from(qPost)
        .where(
            conditions
        );
  }
}