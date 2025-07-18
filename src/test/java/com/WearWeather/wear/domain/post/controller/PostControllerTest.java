package com.WearWeather.wear.domain.post.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.post.dto.request.PostCreateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostUpdateRequest;
import com.WearWeather.wear.domain.post.dto.request.PostsByFiltersRequest;
import com.WearWeather.wear.domain.post.dto.response.PostDetailResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByFiltersResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByLocationResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByMeResponse;
import com.WearWeather.wear.domain.post.dto.response.PostsByTemperatureResponse;
import com.WearWeather.wear.domain.post.dto.response.TopLikedPostResponse;
import com.WearWeather.wear.domain.post.facade.PostCreateFacade;
import com.WearWeather.wear.domain.post.facade.PostDeleteFacade;
import com.WearWeather.wear.domain.post.facade.PostReaderFacade;
import com.WearWeather.wear.domain.post.facade.PostUpdateFacade;
import com.WearWeather.wear.fixture.PostFixture;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)

public class PostControllerTest extends RestDocsTestSupport {

    @MockBean
    private PostCreateFacade postCreateFacade;

    @MockBean
    private PostUpdateFacade postUpdateFacade;

    @MockBean
    private PostDeleteFacade postDeleteFacade;

    @MockBean
    private PostReaderFacade postReaderFacade;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("게시글 생성 API")
    void create() throws Exception {
        // given
        PostCreateRequest request = PostFixture.createPostRequest();

        given(postCreateFacade.createPost(anyLong(), any())).willReturn(1L);

        // when & then
        mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.postId").value(1L))
          .andDo(print())
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("title").description("게시글 제목"),
              fieldWithPath("content").description("게시글 내용"),
              fieldWithPath("temperature").description("작성 당시의 기온"),
              fieldWithPath("gender").description("성별 - MALE 또는 FEMALE"),
              fieldWithPath("city").description("도시 이름 (예: 서울)"),
              fieldWithPath("district").description("구 이름 (예: 강남구)"),
              fieldWithPath("weatherTagIds").description("날씨 태그 ID 리스트"),
              fieldWithPath("temperatureTagIds").description("온도 태그 ID 리스트"),
              fieldWithPath("seasonTagId").description("계절 태그 ID"),
              fieldWithPath("imageIds").description("이미지 ID 리스트")
            ),
            responseFields(
              fieldWithPath("postId").description("생성된 게시글 ID")
            )
          ));
    }

    @Test
    @DisplayName("좋아요 많은 게시글 조회 API")
    void getTopLikedPosts() throws Exception {
        // given
         List<TopLikedPostResponse> topLikedPosts = PostFixture.getTopLikedPostResponses();

        given(postReaderFacade.getTopLikedPosts(anyLong()))
          .willReturn(topLikedPosts);

        // when & then
        mockMvc.perform(get("/posts/top-liked"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.topLikedPosts[0].postId").value(1L))
          .andDo(print())
          .andDo(restDocs.document(
            responseFields(
              subsectionWithPath("topLikedPosts").description("좋아요 많은 게시글 리스트"),
              fieldWithPath("topLikedPosts[].postId").description("게시글 ID"),
              fieldWithPath("topLikedPosts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("topLikedPosts[].location.city").description("도시 이름"),
              fieldWithPath("topLikedPosts[].location.district").description("구 이름"),
              fieldWithPath("topLikedPosts[].seasonTag").description("계절 태그"),
              fieldWithPath("topLikedPosts[].weatherTags").description("날씨 태그 리스트"),
              fieldWithPath("topLikedPosts[].temperatureTags").description("온도 태그 리스트"),
              fieldWithPath("topLikedPosts[].likeByUser").description("현재 유저가 좋아요 눌렀는지 여부")
            )
          ));
    }

    @Test
    @DisplayName("게시글 수정 API")
    void update() throws Exception {
        // given
        Long postId = 1L;
        PostUpdateRequest request = PostFixture.updatePostRequest();

        // when & then
        mockMvc.perform(patch("/posts/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_UPDATE_POST))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("수정할 게시글 ID")
            ),
            requestFields(
              fieldWithPath("title").description("게시글 제목"),
              fieldWithPath("content").description("게시글 내용"),
              fieldWithPath("gender").description("성별 - MALE 또는 FEMALE"),
              fieldWithPath("city").description("도시 이름"),
              fieldWithPath("district").description("구 이름"),
              fieldWithPath("weatherTagIds").description("날씨 태그 ID 리스트"),
              fieldWithPath("temperatureTagIds").description("온도 태그 ID 리스트"),
              fieldWithPath("seasonTagId").description("계절 태그 ID"),
              fieldWithPath("imageIds").description("이미지 ID 리스트")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("게시글 삭제 API")
    void delete() throws Exception {
        // given
        Long postId = 1L;

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/{postId}", postId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_DELETE_POST))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("삭제할 게시글 ID")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("게시글 상세 조회 API")
    void getPostDetail() throws Exception {
        // given
        Long postId = 1L;
        PostDetailResponse response = PostFixture.getPostDetailResponse();

        given(postReaderFacade.getPostDetail(anyLong(), eq(postId))).willReturn(response);

        // when & then
        mockMvc.perform(get("/posts/{postId}", postId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.title").value(response.getTitle()))
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("postId").description("조회할 게시글 ID")
            ),
            responseFields(
              fieldWithPath("nickname").description("작성자 닉네임"),
              fieldWithPath("date").description("작성 일시"),
              fieldWithPath("title").description("게시글 제목"),
              fieldWithPath("content").description("게시글 내용"),
              fieldWithPath("images.image[].imageId").description("이미지 ID"),
              fieldWithPath("images.image[].url").description("이미지 URL"),
              fieldWithPath("location.city").description("도시"),
              fieldWithPath("location.district").description("구"),
              fieldWithPath("seasonTag").description("계절 태그"),
              fieldWithPath("weatherTags").description("날씨 태그 목록"),
              fieldWithPath("temperatureTags").description("온도 태그 목록"),
              fieldWithPath("likeByUser").description("사용자 좋아요 여부"),
              fieldWithPath("likedCount").description("좋아요 수"),
              fieldWithPath("reportPost").description("신고 여부")
            )
          ));
    }

    @Test
    @DisplayName("위치 기반 게시글 조회 API")
    void getPostsByLocation() throws Exception {
        // given
        PostsByLocationResponse response = PostFixture.postsByLocationResponse();

        given(postReaderFacade.getPostsByLocation(anyLong(), anyInt(), anyInt(), anyString(), anyString(), any()))
          .willReturn(response);

        // when & then
        mockMvc.perform(get("/posts")
            .queryParam("page", "0")
            .queryParam("size", "10")
            .queryParam("city", "서울")
            .queryParam("district", "강남구")
            .queryParam("sort", "LATEST"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total").value(response.total()))
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("page").description("페이지 번호 (0부터 시작)"),
              parameterWithName("size").description("페이지 크기"),
              parameterWithName("city").description("도시 이름"),
              parameterWithName("district").description("구 이름"),
              parameterWithName("sort").description("정렬 조건: LATEST, LIKED")
            ),
            responseFields(
              fieldWithPath("location.city").description("도시"),
              fieldWithPath("location.district").description("구"),
              fieldWithPath("posts[].postId").description("게시글 ID"),
              fieldWithPath("posts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("posts[].seasonTag").description("계절 태그"),
              fieldWithPath("posts[].weatherTags").description("날씨 태그 목록"),
              fieldWithPath("posts[].temperatureTags").description("온도 태그 목록"),
              fieldWithPath("posts[].likeByUser").description("사용자 좋아요 여부"),
              fieldWithPath("total").description("총 페이지 수")
            )
          ));
    }

    @Test
    @DisplayName("Tag(지역, 날씨, 온도, 계절) 조건 기반 게시글 검색 API")
    void searchPostsByFilters() throws Exception {
        // given
        PostsByFiltersRequest request = PostFixture.postsByFiltersRequest();
        PostsByFiltersResponse response = PostFixture.getPostsByFiltersResponse();

        given(postReaderFacade.getPosts(anyLong(), any(PostsByFiltersRequest.class)))
          .willReturn(response);

        // when & then
        mockMvc.perform(post("/posts/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.total").value(response.getTotal()))
          .andDo(print())
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("location[].city").description("도시 ID"),
              fieldWithPath("location[].district").description("구 ID"),
              fieldWithPath("seasonTagIds").description("계절 태그 ID 리스트"),
              fieldWithPath("weatherTagIds").description("날씨 태그 ID 리스트"),
              fieldWithPath("temperatureTagIds").description("온도 태그 ID 리스트"),
              fieldWithPath("gender").description("성별 (FEMALE 또는 MALE)"),
              fieldWithPath("sort").description("정렬 조건: LATEST, LIKED"),
              fieldWithPath("page").description("페이지 번호 (0부터 시작)"),
              fieldWithPath("size").description("페이지 크기")
            ),
            responseFields(
              fieldWithPath("posts[].postId").description("게시글 ID"),
              fieldWithPath("posts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("posts[].location.city").description("도시"),
              fieldWithPath("posts[].location.district").description("구"),
              fieldWithPath("posts[].seasonTag").description("계절 태그"),
              fieldWithPath("posts[].weatherTags").description("날씨 태그 리스트"),
              fieldWithPath("posts[].temperatureTags").description("온도 태그 리스트"),
              fieldWithPath("posts[].likeByUser").description("사용자 좋아요 여부"),
              fieldWithPath("posts[].gender").description("성별"),
              fieldWithPath("total").description("총 페이지 수")
            )
          ));
    }

    @Test
    @DisplayName("온도 기반 게시글 조회 API")
    void getPostsByTemperature() throws Exception {
        // given
        int tmp = 20;
        int page = 0;
        int size = 5;

        PostsByTemperatureResponse response = PostFixture.getPostsByTemperatureResponse();

        given(postReaderFacade.getPostsByTemperature(anyLong(), eq(tmp), eq(page), eq(size)))
          .willReturn(response);

        // when & then
        mockMvc.perform(get("/posts/tmp")
            .param("tmp", String.valueOf(tmp))
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size)))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("tmp").description("현재 온도"),
              parameterWithName("page").description("페이지 번호"),
              parameterWithName("size").description("페이지 크기")
            ),
            responseFields(
              fieldWithPath("tmpRangeStart").description("추천 온도 범위 시작"),
              fieldWithPath("tmpRangeEnd").description("추천 온도 범위 끝"),
              fieldWithPath("posts[].postId").description("게시글 ID"),
              fieldWithPath("posts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("posts[].location.city").description("도시"),
              fieldWithPath("posts[].location.district").description("구"),
              fieldWithPath("posts[].seasonTag").description("계절 태그"),
              fieldWithPath("posts[].weatherTags").description("날씨 태그 리스트"),
              fieldWithPath("posts[].temperatureTags").description("온도 태그 리스트"),
              fieldWithPath("posts[].likeByUser").description("사용자 좋아요 여부"),
              fieldWithPath("total").description("총 페이지 수")
            )
          ));
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 API")
    void getPostsByMe() throws Exception {
        // given
        int page = 0;
        int size = 5;

        PostsByMeResponse response = PostFixture.getPostsByMeResponse();

        given(postReaderFacade.getPostsByMe(anyLong(), eq(page), eq(size)))
          .willReturn(response);

        // when & then
        mockMvc.perform(get("/posts/me")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size)))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("page").description("페이지 번호"),
              parameterWithName("size").description("페이지 크기")
            ),
            responseFields(
              fieldWithPath("myPosts[].postId").description("게시글 ID"),
              fieldWithPath("myPosts[].thumbnail").description("썸네일 이미지 URL"),
              fieldWithPath("myPosts[].location.city").description("도시"),
              fieldWithPath("myPosts[].location.district").description("구"),
              fieldWithPath("myPosts[].seasonTag").description("계절 태그"),
              fieldWithPath("myPosts[].weatherTags").description("날씨 태그 리스트"),
              fieldWithPath("myPosts[].temperatureTags").description("온도 태그 리스트"),
              fieldWithPath("myPosts[].likeByUser").description("사용자 좋아요 여부"),
              fieldWithPath("myPosts[].reportPost").description("신고 여부"),
              fieldWithPath("total").description("총 페이지 수")
            )
          ));
    }
}
