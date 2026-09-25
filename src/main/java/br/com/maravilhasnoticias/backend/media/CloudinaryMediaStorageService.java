package br.com.maravilhasnoticias.backend.media;

import br.com.maravilhasnoticias.backend.common.exception.ExternalServiceConfigurationException;
import br.com.maravilhasnoticias.backend.common.exception.InvalidUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryMediaStorageService implements MediaStorageService {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryMediaStorageService(
            @Value("${CLOUDINARY_CLOUD_NAME:}") String cloudName,
            @Value("${CLOUDINARY_API_KEY:}") String apiKey,
            @Value("${CLOUDINARY_API_SECRET:}") String apiSecret
    ) { this.cloudName = cloudName; this.apiKey = apiKey; this.apiSecret = apiSecret; }

    @Override
    public MediaUploadResponse uploadImage(MultipartFile file) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ExternalServiceConfigurationException("O armazenamento de imagens não está configurado");
        }
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new InvalidUploadException("Envie um arquivo de imagem válido");
        }
        if (file.getSize() > MAX_SIZE) throw new InvalidUploadException("A imagem deve possuir no máximo 5 MB");

        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret, "secure", true));
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "maravilhas-noticias", "resource_type", "image"));
            return new MediaUploadResponse(String.valueOf(result.get("secure_url")), String.valueOf(result.get("public_id")));
        } catch (IOException exception) {
            throw new InvalidUploadException("Não foi possível processar a imagem", exception);
        }
    }
}
