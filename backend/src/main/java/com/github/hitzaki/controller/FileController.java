package com.github.hitzaki.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.hitzaki.common.Result;
import com.github.hitzaki.config.MinIOConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@SaCheckLogin
public class FileController {
    
    private final MinioClient minioClient;
    private final MinIOConfig minIOConfig;
    
    // 初始化时确保bucket存在
    @PostConstruct
    public void init() {
        try {
            // 检查bucket是否存在，不存在则创建
            boolean found = minioClient.bucketExists(io.minio.BucketExistsArgs.builder()
                    .bucket(minIOConfig.getBucketName())
                    .build());
            if (!found) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder()
                        .bucket(minIOConfig.getBucketName())
                        .build());
            }
        } catch (Exception e) {
            log.error("MinIO bucket初始化失败", e);
        }
    }
    
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "icon") String type) {
        try {
            // 检查文件
            if (file.isEmpty()) {
                return Result.error("文件为空");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = type + "/" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" +
                    UUID.randomUUID().toString() + extension;
            
            // 上传到MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            // 返回访问URL
            String url = minIOConfig.getEndpoint() + "/" + minIOConfig.getBucketName() + "/" + fileName;
            return Result.success(url);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}

