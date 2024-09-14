package com.WearWeather.wear.domain.postImage.scheduler;

import com.WearWeather.wear.domain.postImage.service.PostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class postImageScheduler {

    private final PostImageService postImageService;

    @Scheduled(cron = "${schedule.cron}")
    public void runDeleteNecessaryImages() {
        postImageService.deleteUnNecessaryImage();
        log.info("Delete necessary images");

    }
}
