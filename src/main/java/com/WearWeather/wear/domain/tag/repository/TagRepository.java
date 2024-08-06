package com.WearWeather.wear.domain.tag.repository;

import com.WearWeather.wear.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
