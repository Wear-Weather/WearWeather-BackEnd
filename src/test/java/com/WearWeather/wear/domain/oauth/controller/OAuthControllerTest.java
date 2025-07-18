package com.WearWeather.wear.domain.oauth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KakaoLoginParam;
import com.WearWeather.wear.domain.oauth.service.OAuthLoginService;
import com.WearWeather.wear.global.jwt.JwtCookieManager;
import com.WearWeather.wear.global.jwt.TokenProvider;
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
@WebMvcTest(OAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class OAuthControllerTest extends RestDocsTestSupport {

    @MockBean
    private OAuthLoginService oAuthLoginService;

    @MockBean
    private JwtCookieManager jwtCookieManager;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("카카오 소셜 로그인 API")
    void kakaoLogin() throws Exception {
        // given
        String authorizationCode = "kakao_auth_code";
        TokenResponse tokenResponse = TokenResponse.of("access-token", "refresh-token");

        given(oAuthLoginService.login(any(KakaoLoginParam.class)))
          .willReturn(tokenResponse);

        // when & then
        mockMvc.perform(get("/oauth/kakao")
            .param("code", authorizationCode))
          .andExpect(status().isOk())
          .andDo(print())
          .andDo(restDocs.document(
            queryParameters(
              parameterWithName("code").description("카카오 인가 코드")
            )
          ));
    }
}
