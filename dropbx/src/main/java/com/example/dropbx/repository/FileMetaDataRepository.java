package com.example.dropbx.repository;

import com.example.dropbx.models.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FileMetaDataRepository extends MongoRepository<FileMetaData, String> {
    Optional<FileMetaData> findByFileName(String fileName);
}
