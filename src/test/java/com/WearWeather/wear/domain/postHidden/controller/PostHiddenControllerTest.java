package com.WearWeather.wear.domain.postHidden.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsConfig;
import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.postHidden.service.PostHiddenService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(PostHiddenController.class)
@Import(RestDocsConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class PostHiddenControllerTest extends RestDocsTestSupport {

    @MockBean
    private PostHiddenService postHiddenService;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void initMocks() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("게시글 숨김 처리 API")
    void hidePost() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/{postId}/hide", postId))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", "/posts/" + postId + "/hide"))
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_POST_HIDDEN))
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("숨길 게시글 ID")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }
}
