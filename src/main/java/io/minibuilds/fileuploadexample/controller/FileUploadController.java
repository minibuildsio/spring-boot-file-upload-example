package io.minibuilds.fileuploadexample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
public class FileUploadController {

    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file)
            throws IOException {
        logger.info("File content type: {}", file.getContentType());
        logger.info("File size: {}", file.getSize());

        return Map.of("contents", new String(file.getBytes()));
    }
}
