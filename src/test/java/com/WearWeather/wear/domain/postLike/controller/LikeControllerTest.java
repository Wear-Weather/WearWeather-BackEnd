package com.WearWeather.wear.domain.postLike.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.post.dto.response.LocationResponse;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostByMeResponse;
import com.WearWeather.wear.domain.postLike.dto.response.LikedPostsByMeResponse;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterLike;
import com.WearWeather.wear.domain.postLike.dto.response.TotalLikedCountAfterUnlike;
import com.WearWeather.wear.domain.postLike.facade.LikeCreateFacade;
import com.WearWeather.wear.domain.postLike.facade.LikeDeleteFacade;
import com.WearWeather.wear.domain.postLike.facade.LikeReaderFacade;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
import java.util.List;
import java.util.Map;
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
@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class LikeControllerTest extends RestDocsTestSupport {

    @MockBean
    private LikeReaderFacade likeReaderFacade;

    @MockBean
    private LikeCreateFacade likeCreateFacade;

    @MockBean
    private LikeDeleteFacade likeDeleteFacade;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("게시글 좋아요 추가 API")
    void addLike() throws Exception {
        given(likeCreateFacade.addLike(anyLong(), anyLong()))
          .willReturn(TotalLikedCountAfterLike.of(10));

        mockMvc.perform(post("/likes/posts/{postId}", 1L))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.likedCount").value(10))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("게시글 ID")
            ),
            responseFields(
              fieldWithPath("likedCount").description("좋아요 수")
            )
          ));
    }

    @Test
    @DisplayName("게시글 좋아요 취소 API")
    void removeLike() throws Exception {
        given(likeDeleteFacade.removeLike(anyLong(), anyLong()))
          .willReturn(new TotalLikedCountAfterUnlike(7));

        mockMvc.perform(delete("/likes/posts/{postId}", 1L))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.likedCount").value(7))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("게시글 ID")
            ),
            responseFields(
              fieldWithPath("likedCount").description("좋아요 수")
            )
          ));
    }

    @Test
    @DisplayName("내가 좋아요한 게시글 조회 API")
    void getLikedPostsByMe() throws Exception {
        LocationResponse location = LocationResponse.of("서울", "강남구");
        LikedPostByMeResponse post = LikedPostByMeResponse.of(
          1L,
          "thumbnail.jpg",
          location,
          Map.of(
            "SEASON", List.of("봄"),
            "WEATHER", List.of("맑음", "흐림"),
            "TEMPERATURE", List.of("따뜻함")
          ),
          true
        );
        LikedPostsByMeResponse response = LikedPostsByMeResponse.of(List.of(post), 1);

        given(likeReaderFacade.getLikedPostsByMe(anyLong(), eq(0), eq(5)))
          .willReturn(response);

        mockMvc.perform(get("/likes/posts")
            .param("page", "0")
            .param("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.myLikedPosts[0].postId").value(1L))
          .andExpect(jsonPath("$.myLikedPosts[0].seasonTag").value("봄"))
          .andExpect(jsonPath("$.total").value(1))
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("page").description("페이지 번호"),
              parameterWithName("size").description("페이지 크기")
            ),
            responseFields(
              fieldWithPath("myLikedPosts[].postId").description("게시글 ID"),
              fieldWithPath("myLikedPosts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("myLikedPosts[].location.city").description("도시 이름"),
              fieldWithPath("myLikedPosts[].location.district").description("구 이름"),
              fieldWithPath("myLikedPosts[].seasonTag").description("계절 태그"),
              fieldWithPath("myLikedPosts[].weatherTags").description("날씨 태그 리스트"),
              fieldWithPath("myLikedPosts[].temperatureTags").description("온도 태그 리스트"),
              fieldWithPath("myLikedPosts[].likeByUser").description("유저가 좋아요했는지 여부"),
              fieldWithPath("total").description("총 페이지 수")
            )
          ));
    }
}
