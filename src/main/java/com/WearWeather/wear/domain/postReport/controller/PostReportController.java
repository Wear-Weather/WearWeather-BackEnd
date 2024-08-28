package com.WearWeather.wear.domain.postReport.controller;


import com.WearWeather.wear.domain.postReport.service.PostReportService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts/{postId}/report")
@RestController
@RequiredArgsConstructor
public class PostReportController {

    private final PostReportService postReportService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> reportPost(@LoggedInUser Long userId, @PathVariable Long postId, @RequestParam String reason) throws URISyntaxException {
        postReportService.reportPost(userId, postId, reason);
        URI location = new URI("/posts/" + postId + "/report");
        return ResponseEntity.created(location).body(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_REPORT_POST));
    }
}
