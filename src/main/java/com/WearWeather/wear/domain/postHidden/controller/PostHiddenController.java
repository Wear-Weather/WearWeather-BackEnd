package com.WearWeather.wear.domain.postHidden.controller;


import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
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
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts/{postId}/hide")
@RequiredArgsConstructor
@RestController
public class PostHiddenController {

    private final PostHiddenService postHiddenService;

    @PostMapping
    public ResponseEntity<ResponseCommonDTO> hidePost(@LoggedInUser Long userId, @PathVariable("postId") Long postId) throws URISyntaxException {
        postHiddenService.hidePost(userId, postId);
        URI location = new URI("/posts/" + postId + "/hide");
        return ResponseEntity.created(location).body(new ResponseCommonDTO(true, ResponseMessage.SUCCESS_POST_HIDDEN));
    }

}
