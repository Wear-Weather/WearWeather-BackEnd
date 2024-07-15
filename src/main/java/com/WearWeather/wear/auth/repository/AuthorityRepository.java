package com.WearWeather.wear.auth.repository;

import com.WearWeather.wear.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
