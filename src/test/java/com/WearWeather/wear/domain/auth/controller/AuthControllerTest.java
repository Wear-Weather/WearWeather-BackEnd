package com.WearWeather.wear.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsConfig;
import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.facade.LogOutFacade;
import com.WearWeather.wear.domain.auth.facade.LoginFacade;
import com.WearWeather.wear.domain.auth.facade.ReissueFacade;
import com.WearWeather.wear.global.jwt.JwtCookieManager;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
import jakarta.servlet.http.Cookie;
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
import org.springframework.http.MediaType;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(AuthController.class)
@Import(RestDocsConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
class AuthControllerTest extends RestDocsTestSupport {

    @MockBean
    private LoginFacade loginFacade;

    @MockBean
    private LogOutFacade logOutFacade;

    @MockBean
    private ReissueFacade reissueFacade;

    @MockBean
    private JwtCookieManager jwtCookieManager;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @BeforeEach
    void setUp() throws Exception {
        given(loggedInUserArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loggedInUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@email.com", "password123");
        TokenResponse response = new TokenResponse("access-token", "refresh-token");

        given(loginFacade.checkLogin(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("email").description("사용자 이메일"),
              fieldWithPath("password").description("비밀번호")
            )
          ));
    }

    @Test
    @DisplayName("로그아웃 API")
    void logout() throws Exception {
        // given
        String tokenHeader = "Bearer dummy-token";

        // when & then
        mockMvc.perform(post("/auth/logout")
            .header("Authorization", tokenHeader))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("success logout"))
          .andDo(print())
          .andDo(restDocs.document(
            requestHeaders(
              headerWithName("Authorization").description("Bearer 토큰")
            ),
            responseFields(
              fieldWithPath("success").description("성공 여부"),
              fieldWithPath("message").description("로그아웃 결과 메시지")
            )
          ));
    }

    @Test
    @DisplayName("토큰 재발급 API")
    void reissue() throws Exception {
        // given
        Cookie refreshTokenCookie = new Cookie("refreshToken", "dummy-refresh-token");

        given(reissueFacade.reissue(anyString())).willReturn("new-access-token");

        // when & then
        mockMvc.perform(post("/auth/reissue")
            .cookie(refreshTokenCookie))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document(
            requestCookies(
              cookieWithName("refreshToken").description("리프레시 토큰 쿠키")
            )
          ));
    }
}
