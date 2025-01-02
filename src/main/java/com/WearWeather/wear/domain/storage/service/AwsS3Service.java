package com.WearWeather.wear.domain.storage.service;

import com.WearWeather.wear.domain.storage.dto.ImageInfoDto;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sksamuel.scrimage.webp.WebpWriter;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sksamuel.scrimage.ImmutableImage;

import java.io.File;
import java.io.FileOutputStream;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${photo.dir}")
    private String photoDir;

    private final AmazonS3 amazonS3;

    @PostConstruct
    private void init() {
        System.out.println("Java Temp Dir: " + System.getProperty("java.io.tmpdir"));
        File tempDir = new File(photoDir, "post-image"); // 하위 디렉토리 포함
        if (!tempDir.exists()) {
            boolean dirCreated = tempDir.mkdirs();
            System.out.println("PhotoDir Created: " + tempDir.getAbsolutePath() + " -> " + dirCreated);
            if (!dirCreated) {
                throw new RuntimeException("Failed to create directory: " + photoDir);
            }
        }
    }
    
    /**
     * 업로드할 파일을 WebP로 변환 후 S3에 저장
     *
     * @param multipartFile : 파일
     * @param dirName       : S3 폴더 이름
     * @return : ImageInfoDto (path, cdn URL 등)
     */
    public ImageInfoDto upload(MultipartFile multipartFile, String dirName) {

        // MultipartFile을 File로 변환
        File originalFile = convertMultipartFileToFile(multipartFile);

        String baseFileName = createFileName(multipartFile.getOriginalFilename(), dirName);
        String webpFileName = baseFileName + ".webp";

        // WebP로 변환
        File webpFile = convertToWebp(webpFileName, originalFile);

        // S3에 업로드
        try {
            uploadFileToS3(webpFile, webpFileName);
            String url = amazonS3.getUrl(bucket, webpFileName).toString();

            return ImageInfoDto.of(webpFileName, url);
        } finally {
            originalFile.delete();
            webpFile.delete();
        }
    }

    private File convertToWebp(String fileName, File originalFile) {
        try {
            // WebP 저장 파일 경로 생성
            File outputFile = new File(photoDir, fileName);
            File outputDir = outputFile.getParentFile();

            // 디렉토리 확인 및 생성
            if (!outputDir.exists()) {
                boolean dirCreated = outputDir.mkdirs();
                System.out.println("Output Directory Created: " + outputDir.getAbsolutePath() + " -> " + dirCreated);
                if (!dirCreated) {
                    throw new IOException("Failed to create directory: " + outputDir.getAbsolutePath());
                }
            }

            // WebP 변환 수행
            System.out.println("Converting to WebP: " + originalFile.getAbsolutePath());
            return ImmutableImage.loader()
              .fromFile(originalFile)
              .output(WebpWriter.DEFAULT, outputFile);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Image Format: " + e.getMessage());
            throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        } catch (IOException e) {
            System.out.println("WebP Conversion Failed: " + e.getMessage());
            throw new CustomException(ErrorCode.WEBP_CONVERSION_FAILED);
        }
    }




    private void uploadFileToS3(File file, String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType("image/webp");

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, fileInputStream, metadata));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        try {
            File convFile = new File(file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }
            return convFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_IMAGE_FILE);
        }
    }

    private String createFileName(String fileName, String dirName) {
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        return dirName + "/" + UUID.randomUUID() + "_" + baseName;
    }

    public void delete(String fileName) {
        amazonS3.deleteObject(bucket, fileName);
    }

    public String getUrl(String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
