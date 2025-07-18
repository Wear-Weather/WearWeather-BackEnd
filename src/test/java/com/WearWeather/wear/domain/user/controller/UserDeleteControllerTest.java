package com.WearWeather.wear.domain.user.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.user.facade.UserDeleteFacade;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.jwt.UserIdArgumentResolver;
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
@WebMvcTest(UserDeleteController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserDeleteControllerTest extends RestDocsTestSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private UserDeleteFacade userDeleteFacade;

    @MockBean
    private UserIdArgumentResolver loggedInUserArgumentResolver;

    @Test
    @DisplayName("회원 탈퇴 사유 조회 API")
    void get_delete_reasons() throws Exception {
        mockMvc.perform(get("/users/delete-reasons"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0]").value("사용을 잘 안하게 돼요"))
          .andExpect(jsonPath("$[1]").value("서비스 활성화가 잘 안되어 있어요"))
          .andExpect(jsonPath("$[2]").value("개인정보 보호를 위해 삭제할 필요가 있어요"))
          .andExpect(jsonPath("$[3]").value("서비스 기능이 미흡해요"))
          .andExpect(jsonPath("$[4]").value("오류가 잦아요"))
          .andDo(restDocs.document(
            responseFields(
              fieldWithPath("[].").description("회원 탈퇴 사유 목록")
            )
          ));
    }
}
