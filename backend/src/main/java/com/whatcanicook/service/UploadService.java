package com.whatcanicook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class UploadService {

    private final Path uploadsRoot;

    public UploadService(@Value("${app.uploads.dir:uploads}") String uploadsDir) {
        this.uploadsRoot = Paths.get(uploadsDir).toAbsolutePath().normalize();
    }

    public Path getUploadsRoot() {
        return uploadsRoot;
    }

    public String storeImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }

        String contentType = file.getContentType();
        String extension = extensionForContentType(contentType);
        if (extension == null) {
            throw new IllegalArgumentException("Tipo de archivo no permitido");
        }

        String filename = UUID.randomUUID() + extension;
        Path targetDir = uploadsRoot.resolve(folder).normalize();
        if (!targetDir.startsWith(uploadsRoot)) {
            throw new IllegalArgumentException("Ruta de subida inválida");
        }

        try {
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Error guardando archivo", e);
        }

        return "/uploads/" + folder + "/" + filename;
    }

    private String extensionForContentType(String contentType) {
        if (contentType == null) return null;
        String normalized = contentType.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case "image/webp" -> ".webp";
            default -> null;
        };
    }
}

