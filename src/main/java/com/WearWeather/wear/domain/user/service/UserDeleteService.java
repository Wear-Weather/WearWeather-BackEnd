package com.WearWeather.wear.domain.user.service;

import com.WearWeather.wear.domain.user.enums.DeleteReason;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.entity.UserDelete;
import com.WearWeather.wear.domain.user.repository.UserDeleteRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeleteService {

    private final UserDeleteRepository userDeleteRepository;

    public void save(User user, DeleteReason deleteReason) {

        if (getUserDeleteByUserId(user.getUserId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_DELETE_USER);
        }

        UserDelete userDelete = UserDelete.builder()
            .userId(user.getUserId())
            .deleteReason(deleteReason.getDescription())
            .build();

        userDeleteRepository.save(userDelete);
    }

    public Optional<UserDelete> getUserDeleteByUserId(Long userId){
        return userDeleteRepository.findByUserId(userId);
    }

}
