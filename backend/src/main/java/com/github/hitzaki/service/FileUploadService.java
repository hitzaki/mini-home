package com.github.hitzaki.service;

import com.github.hitzaki.exception.BusinessException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {
    
    private final MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String endpoint;
    
    /**
     * 上传文件到MinIO
     */
    public String uploadFile(MultipartFile file, String type) throws Exception {
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件为空");
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = type + "/" + UUID.randomUUID().toString() + extension;
        
        // 确保bucket存在
        ensureBucketExists();
        
        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
        }
        
        // 返回文件URL
        String url = endpoint + "/" + bucketName + "/" + fileName;
        log.info("文件上传成功: {}", url);
        return url;
    }
    
    /**
     * 确保bucket存在
     */
    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(io.minio.BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!exists) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("创建bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("检查或创建bucket失败", e);
            throw new BusinessException(500, "文件存储服务异常，请稍后重试", e);
        }
    }
}

