package org.shanling.simplemanage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件访问控制器
 * 
 * @author shanling
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file.upload.path:D:/uploads/scripts}")
    private String uploadPath;

    /**
     * 访问脚本文件
     */
    @GetMapping("/scripts/{gameId}/{dateFolder}/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String gameId,
            @PathVariable String dateFolder,
            @PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, gameId, dateFolder, filename);
            File file = filePath.toFile();
            
            if (!file.exists()) {
                log.warn("文件不存在：{}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            log.error("下载文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
