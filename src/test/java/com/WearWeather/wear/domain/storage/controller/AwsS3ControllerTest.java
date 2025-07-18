package com.WearWeather.wear.domain.storage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.WearWeather.wear.document.utils.RestDocsTestSupport;
import com.WearWeather.wear.domain.postImage.service.PostImageService;
import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.domain.storage.service.AwsS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(AwsS3Controller.class)
@AutoConfigureMockMvc(addFilters = false)
class AwsS3ControllerTest extends RestDocsTestSupport {

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private PostImageService postImageService;

    @Test
    @DisplayName("게시글 이미지 업로드 API")
    void uploadPostImage() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
          "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes()
        );

        ImageInfoDto imageInfoDto = ImageInfoDto.of("s3-name", "https://s3.bucket.com/image.jpg");
        given(awsS3Service.upload(any(), eq("post-image"))).willReturn(imageInfoDto);
        given(postImageService.createPostImage(any(), eq(imageInfoDto))).willReturn(1L);

        // when & then
        mockMvc.perform(multipart("/s3/post-image")
            .file(file))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.url").value("https://s3.bucket.com/image.jpg"))
          .andDo(print())
          .andDo(restDocs.document(
            requestParts(
              partWithName("file").description("업로드할 이미지 파일")
            ),
            responseFields(
              fieldWithPath("id").description("업로드된 이미지 ID"),
              fieldWithPath("url").description("S3 이미지 URL")
            )
          ));
    }

    @Test
    @DisplayName("게시글 이미지 삭제 API")
    void deletePostImage() throws Exception {
        // given
        Long imageId = 1L;
        willDoNothing().given(postImageService).deleteImage(imageId);

        // when & then
        mockMvc.perform(delete("/s3/post-image/{imageId}", imageId))
          .andExpect(status().isNoContent())
          .andDo(print())
          .andDo(restDocs.document(
            pathParameters(
              parameterWithName("imageId").description("삭제할 이미지 ID")
            )
          ));
    }
}
