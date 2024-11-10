package com.example.dropbx.controllers;

import com.example.dropbx.models.FileMetaData;
import com.example.dropbx.services.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileMetaData metadata = fileStorageService.uploadFile(file);
            return ResponseEntity.ok(metadata);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileMetaData>> getAllFiles() {
        List<FileMetaData> files = fileStorageService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/file/{fileName}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws MalformedURLException {

        FileMetaData metadata = fileStorageService.getFile(fileName)
                .orElseThrow(() -> new RuntimeException("File not found with fileName " + fileName));

        Path filePath = Paths.get(metadata.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFileName() + "\"")
                .body(resource);
    }
}
