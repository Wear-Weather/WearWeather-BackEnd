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
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
@RequiredArgsConstructor
public class PostByFilterRepositoryCustomImpl implements PostByFilterRepositoryCustom{
  private final JPAQueryFactory jpaQueryFactory;

  QPost qPost = QPost.post;
  QPostTag qPostTag = QPostTag.postTag;
  QCity qCity = QCity.city1;
  QDistrict qDistrict = QDistrict.district1;

  @Override
  public Page<PostWithLocationName> findPostsByFilters (
      PostsByFiltersRequest request, Pageable pageable, List<Long> hiddenPostIds) {
    BooleanBuilder conditions = new BooleanBuilder();

    // 태그 조건 추가
    addTagConditions(request, conditions);

    // 위치 조건 추가
    addLocationConditions(request, conditions);

    // 숨겨진 포스트 조건 추가
    if (!hiddenPostIds.isEmpty()) {
      conditions.and(qPost.id.notIn(hiddenPostIds));
    }

    List<PostWithLocationName> posts = jpaQueryFactory
        .select(Projections.constructor(PostWithLocationName.class,
            qPost.id.as("postId"),
            qPost.thumbnailImageId.as("thumbnailImageId"),
            qCity.city.as("cityName"),
            qDistrict.district.as("districtName")))
        .from(qPost)
        .leftJoin(qCity).on(qPost.location.city.eq(qCity.id))
        .leftJoin(qDistrict).on(qPost.location.district.eq(qDistrict.id))
        .where(conditions)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(getSortColumn(pageable.getSort()))
        .fetch();

    long totalCount = jpaQueryFactory
        .select(qPost.count())
        .from(qPost)
        .where(conditions)
        .fetchOne();

    return PageableExecutionUtils.getPage(posts, pageable, () -> totalCount);
  }

  private void addTagConditions(PostsByFiltersRequest request, BooleanBuilder conditions) {
    List<Long> seasonTagIds = request.getSeasonTagIds();
    List<Long> weatherTagIds = request.getWeatherTagIds();
    List<Long> temperatureTagIds = request.getTemperatureTagIds();

    if (!seasonTagIds.isEmpty()) {
      conditions.or(qPostTag.tagId.in(seasonTagIds));
    }
    if (!weatherTagIds.isEmpty()) {
      conditions.or(qPostTag.tagId.in(weatherTagIds));
    }
    if (!temperatureTagIds.isEmpty()) {
      conditions.or(qPostTag.tagId.in(temperatureTagIds));
    }
  }

  private void addLocationConditions(PostsByFiltersRequest request, BooleanBuilder conditions) {
    List<Location> locationList = request.getLocationList().stream()
        .map(LocationRequest::toEntity)
        .toList();

    if (!locationList.isEmpty()) {
      conditions.and(checkSearchAllDistrictInCity(locationList));
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
}
