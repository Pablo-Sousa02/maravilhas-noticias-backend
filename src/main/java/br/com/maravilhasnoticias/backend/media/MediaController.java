package br.com.maravilhasnoticias.backend.media;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
public class MediaController {
    private final MediaStorageService mediaStorageService;
    public MediaController(MediaStorageService mediaStorageService) { this.mediaStorageService = mediaStorageService; }
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MediaUploadResponse upload(@RequestPart("file") MultipartFile file) { return mediaStorageService.uploadImage(file); }
}
