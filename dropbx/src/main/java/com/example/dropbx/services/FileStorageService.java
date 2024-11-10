package com.example.dropbx.services;

import com.example.dropbx.models.FileMetaData;
import com.example.dropbx.repository.FileMetaDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileMetaDataRepository fileMetadataRepository;

    public FileStorageService(FileMetaDataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        if (!isValidFileType(file.getContentType())) {
            throw new IOException("Unsupported file type. Only PDF, CSV, JPEG, and PNG are allowed.");
        }

        Path tempDir = Files.createTempDirectory("dropbox_uploads_");

        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = tempDir.resolve(uniqueFileName);

        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage());
            throw e;
        }

        FileMetaData metadata = new FileMetaData();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFileType(file.getContentType());
        metadata.setFilePath(filePath.toString());

        return fileMetadataRepository.save(metadata);
    }

    public List<FileMetaData> getAllFiles() {
        return fileMetadataRepository.findAll();
    }

    public Optional<FileMetaData> getFile(String fileName) {
        return fileMetadataRepository.findByFileName(fileName);
    }

    private boolean isValidFileType(String fileType) {
        return "application/pdf".equals(fileType) ||
                "text/csv".equals(fileType) ||
                "image/jpeg".equals(fileType) ||
                "image/png".equals(fileType);
    }
}
