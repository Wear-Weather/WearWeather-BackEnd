package com.WearWeather.wear.domain.postReport.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.postReport.service.PostReportService;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(PostReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostReportControllerTest extends RestDocsTestSupport {

    @MockBean
    private PostReportService postReportService;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("게시글 신고 API")
    void reportPost() throws Exception {
        // given
        Long postId = 1L;
        String reason = "스팸입니다.";

        // when & then
        mockMvc.perform(post("/posts/{postId}/report?reason={reason}", postId, reason)
            .characterEncoding("UTF-8"))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", "/posts/" + postId + "/report"))
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_REPORT_POST))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("신고할 게시글 ID")
            ),
            queryParameters(
              parameterWithName("reason").description("신고 사유")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }
}
