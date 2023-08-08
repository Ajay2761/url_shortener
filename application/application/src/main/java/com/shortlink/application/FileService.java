package com.shortlink.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
  @Autowired
  private FileRepository fileRepository;

  // As I didn't have a free cloud storage account like aws s3,
  // therefore I choose to store the file data in database only
  public ShortLinkResponse saveFileAndGenerateShortLink(MultipartFile file) {
    String originalFilename =
        StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    String shortLink = generateShortLink();

    try {
      byte[] fileContent = file.getBytes();
      String encodedFileData = Base64.getEncoder().encodeToString(fileContent);

      FileEntity fileEntity = new FileEntity();
      fileEntity.setShortLink(shortLink);
      fileEntity.setOriginalName(originalFilename);
      fileEntity.setExtension(extension);
      fileEntity.setFileData(encodedFileData);
      fileRepository.save(fileEntity);

      // Adding the hard coded base url as I didn't have a server to deploy this code,
      // I am still exploring the options available online to deploy this
      return new ShortLinkResponse("http://localhost:8080/api/" + shortLink, "/api/" + shortLink);
    } catch (IOException e) {
      throw new IllegalStateException("Could not read file " + file.getOriginalFilename(), e);
    }
  }

  public FileEntity getFileFromShortLink(String shortLink) {
    return fileRepository.findByShortLink(shortLink);
  }

  private String generateShortLink() {

    long timestamp = System.currentTimeMillis();
    int randomValue = new Random().nextInt(Integer.MAX_VALUE);

    String input = timestamp + "_" + randomValue;

    return generateHash(input);
  }


  private String generateHash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashBytes = md.digest(input.getBytes());
      StringBuilder hashString = new StringBuilder();

      for (byte b : hashBytes) {
        hashString.append(String.format("%02x", b));
      }

      return hashString.toString().substring(0, 8); // Use the first 8 characters
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Hashing algorithm not found.", e);
    }
  }
}


