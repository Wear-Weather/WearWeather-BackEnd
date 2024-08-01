package com.WearWeather.wear.domain.storage.service;

import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /**
     * AWS S3에 이미지 파일 업로드
     *
     * @param multipartFile : 파일
     * @param dirName       : 폴더 이름
     * @return : FileInfoDto (path, cdn URL)
     */
    public ImageInfoDto upload(MultipartFile multipartFile, String dirName) {

        // 이미지 파일 유효성 체크 후 bufferedImage 반환
        BufferedImage bufferedImage = getValidBufferedImage(multipartFile);

        String fileName = createFileName(multipartFile.getOriginalFilename(), dirName);

        // 메타데이터 지정, TODO : 필요여부 체크
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        // s3에 이미지 저장
        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            // TODO : 추주 예외 변경
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        // s3에 저장된 파일 URL 얻어옴
        // TODO : 이미지 조회에 대한 캐싱 적용을 위한 CDN 적용 필요
        String url = amazonS3.getUrl(bucket, fileName).toString();

        return ImageInfoDto.of(fileName, url, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    /**
     * 이미지 유효성 체크 후 BufferedImage 반환
     */
    public BufferedImage getValidBufferedImage(MultipartFile multipartFile) {
        try {
            // 업로드된 파일의 BufferedImage 를 가져온다.
            BufferedImage read = ImageIO.read(multipartFile.getInputStream());

            // 업로드된 파일이 이미지가 아닐 경우를 체크한다.
            if (read == null) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
            }

            // 파일 확장자 체크 [jpeg, jpg, png, gif]
            if (!isValidImageExtension(multipartFile.getContentType())) {
                throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
            }

            return read;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    private String createFileName(String fileName, String dirName) {
        // 파일명을 다르게 하기 위해 UUID 를 붙임
        return dirName + "/" + UUID.randomUUID() + fileName;
    }

    private boolean isValidImageExtension(String contentType) {
        // 유효한 이미지 확장자.
        return contentType.matches("^image/(jpeg|jpg|png|gif)$");
    }

}
