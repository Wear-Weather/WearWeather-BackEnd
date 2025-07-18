package com.WearWeather.wear.domain.mail.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.mail.dto.request.VerifyEmailAuthCodeRequest;
import com.WearWeather.wear.domain.mail.dto.request.VerifyEmailRequest;
import com.WearWeather.wear.domain.mail.service.MailService;
import com.WearWeather.wear.global.common.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest extends RestDocsTestSupport {

    @MockBean
    private MailService mailService;

    @Test
    @DisplayName("이메일 인증 메일 발송 API")
    void send_verification() throws Exception {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest("test@example.com");

        // when & then
        mockMvc.perform(post("/email/send-verification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SEND_EMAIL))
          .andDo(print())
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("email").description("사용자 이메일")
            ),
            responseFields(
              fieldWithPath("success").description("성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 API")
    void verify_code() throws Exception {
        // given
        VerifyEmailAuthCodeRequest request = new VerifyEmailAuthCodeRequest("test@example.com", "123456");

        // when & then
        mockMvc.perform(post("/email/verify-code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SUCCESS_EMAIL_VERIFICATION))
          .andDo(print())
          .andDo(restDocs.document(
            requestFields(
              fieldWithPath("email").description("사용자 이메일"),
              fieldWithPath("code").description("이메일 인증 코드")
            ),
            responseFields(
              fieldWithPath("success").description("성공 여부"),
              fieldWithPath("message").description("응답 메시지")
            )
          ));
    }
}
