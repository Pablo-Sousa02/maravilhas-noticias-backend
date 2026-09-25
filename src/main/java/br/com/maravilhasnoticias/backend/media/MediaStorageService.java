package br.com.maravilhasnoticias.backend.media;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {
    MediaUploadResponse uploadImage(MultipartFile file);
}
