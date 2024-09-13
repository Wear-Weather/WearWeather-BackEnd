package com.WearWeather.wear.domain.user.repository;

import com.WearWeather.wear.domain.user.entity.DeleteReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteReasonRepository extends JpaRepository<DeleteReason, Long> {
}
