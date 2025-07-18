package com.WearWeather.wear.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.user.dto.request.FindUserEmailRequest;
import com.WearWeather.wear.domain.user.dto.request.FindUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserInfoRequest;
import com.WearWeather.wear.domain.user.dto.request.ModifyUserPasswordRequest;
import com.WearWeather.wear.domain.user.dto.request.RegisterUserRequest;
import com.WearWeather.wear.domain.user.dto.response.UserIdForPasswordUpdateResponse;
import com.WearWeather.wear.domain.user.dto.response.UserInfoResponse;
import com.WearWeather.wear.domain.user.facade.UserDeleteFacade;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.common.ResponseMessage;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private UserDeleteFacade userDeleteFacade;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("회원가입 API")
    void signup() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest("test@email.com", "pw1234", "홍길동", "길동이", false);

        mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_USER))
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("email").description("이메일"),
              fieldWithPath("password").description("비밀번호"),
              fieldWithPath("name").description("이름"),
              fieldWithPath("nickname").description("닉네임"),
              fieldWithPath("social").description("소셜 로그인 여부") // 여기!
            )
            ,
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("닉네임 중복 확인 API")
    void checkDuplicateNickname() throws Exception {
        String nickname = "길동이";
        BDDMockito.willDoNothing().given(userService).checkDuplicatedUserNickName(nickname);

        mockMvc.perform(get("/users/nickname-check/{nickname}", nickname))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.available").value(true)) // <-- 수정
          .andExpect(jsonPath("$.message").value(ResponseMessage.NICKNAME_AVAILABLE))
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("nickname").description("중복 확인할 닉네임")
            ),
            responseFields(
              fieldWithPath("available").description("사용 가능 여부"), // <-- 수정
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("이메일 찾기 API")
    void findUserEmail() throws Exception {
        FindUserEmailRequest request = new FindUserEmailRequest("홍길동", "길동이");
        BDDMockito.given(userService.findUserEmail(any(), any())).willReturn("test@email.com");

        mockMvc.perform(post("/users/email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("test@email.com"))
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("name").description("사용자 이름"),
              fieldWithPath("nickname").description("사용자 닉네임")
            ),
            responseFields(
              fieldWithPath("email").description("조회된 사용자 이메일")
            )
          ));
    }

    @Test
    @DisplayName("비밀번호 찾기 API")
    void findUserPassword() throws Exception {
        FindUserPasswordRequest request = new FindUserPasswordRequest("test@email.com", "홍길동", "길동이");
        BDDMockito.given(userService.findUserPassword(any(), any(), any())).willReturn(UserIdForPasswordUpdateResponse.of(1L));

        mockMvc.perform(post("/users/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.userId").value(1L))
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("email").description("사용자 이메일"),
              fieldWithPath("name").description("사용자 이름"),
              fieldWithPath("nickname").description("사용자 닉네임")
            ),
            responseFields(
              fieldWithPath("userId").description("조회된 사용자 ID")
            )
          ));
    }

    @Test
    @DisplayName("비밀번호 변경 API")
    void modify_user_password() throws Exception {
        ModifyUserPasswordRequest request = new ModifyUserPasswordRequest(1L, "newPassword123");

        mockMvc.perform(patch("/users/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.MODIFY_PASSWORD))
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("userId").description("회원 ID"),
              fieldWithPath("password").description("변경할 비밀번호")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("내 정보 조회 API")
    void get_user_info() throws Exception {
        given(userService.getUserInfo(anyLong()))
          .willReturn(new UserInfoResponse("test@email.com", "홍길동", "길동이", false));

        mockMvc.perform(get("/users/me"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value("test@email.com"))
          .andExpect(jsonPath("$.name").value("홍길동"))
          .andExpect(jsonPath("$.nickname").value("길동이"))
          .andExpect(jsonPath("$.social").value(false))
          .andDo(restDocs.document(
            responseFields(
              fieldWithPath("email").description("이메일"),
              fieldWithPath("name").description("이름"),
              fieldWithPath("nickname").description("닉네임"),
              fieldWithPath("social").description("소셜 로그인 여부")
            )
          ));
    }

    @Test
    @DisplayName("내 정보 수정 API")
    void modify_user_info() throws Exception {
        ModifyUserInfoRequest request = new ModifyUserInfoRequest("newPassword123", "길동이");

        mockMvc.perform(patch("/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.MODIFY_USERINFO))
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("password").description("비밀번호"),
              fieldWithPath("nickname").description("변경할 닉네임")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void delete_user() throws Exception {
        mockMvc.perform(
            org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
              .delete("/users")
              .queryParam("deleteReason", "서비스가 불편해요"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_DELETE_USER))
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("deleteReason").description("회원 탈퇴 사유")
            ),
            responseFields(
              fieldWithPath("success").description("요청 성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }
}
