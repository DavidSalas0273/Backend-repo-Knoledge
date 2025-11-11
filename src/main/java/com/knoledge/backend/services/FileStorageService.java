package com.knoledge.backend.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${storage.upload-dir:uploads}") String uploadRoot) throws IOException {
        this.uploadDir = Paths.get(uploadRoot).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    public String store(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        Path targetDirectory = uploadDir.resolve(subDirectory).normalize();
        Files.createDirectories(targetDirectory);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path targetFile = targetDirectory.resolve(filename).normalize();
        file.transferTo(targetFile.toFile());
        return (subDirectory + "/" + filename).replace(File.separatorChar, '/');
    }

    public Path resolve(String relativePath) {
        return uploadDir.resolve(relativePath).normalize();
    }

    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path filePath = resolve(relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // No interrumpimos el flujo si el archivo ya no existe
        }
    }
}
