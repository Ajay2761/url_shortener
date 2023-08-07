package com.shortlink.application;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

  @RestController
  @RequestMapping("/api")
  public class FileUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
      // Check file size (e.g., limit to 10MB)
      long maxSizeInBytes = 10 * 1024 * 1024; // 10MB
      if (file.getSize() > maxSizeInBytes) {
        return ResponseEntity.badRequest().body("File size exceeds the limit of 10MB.");
      }

      ShortLinkResponse response = fileService.saveFileAndGenerateShortLink(file);

      return ResponseEntity.ok().body(response);
    }


    // Initially I thought to return just the base64 and the file name, but later since this is a get api,
    // which can be directly called from any browser, i didn't built another screen to display the file,
    // instead calling this api would directly download it.
    @GetMapping("/{shortLink}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String shortLink) throws IOException {
      FileEntity file = fileService.getFileFromShortLink(shortLink);
      if (file == null) {
        return ResponseEntity.notFound().build();
      }

      byte[] fileData = Base64.getDecoder().decode(file.getFileData());
      InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileData));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentDispositionFormData("attachment", file.getOriginalName());

      return ResponseEntity.ok()
          .headers(headers)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);
    }
  }


