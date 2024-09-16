package com.WearWeather.wear.domain.user.controller;

import com.WearWeather.wear.domain.user.enums.DeleteReason;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserDeleteController {

    @GetMapping("/delete-reasons")
    public ResponseEntity<List<String>> getDeleteReasons() {
        List<String> reasons = Arrays.stream(DeleteReason.values())
            .map(DeleteReason::getDescription)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reasons);
    }
}