package bot.telegram.controller;

import bot.telegram.models.Image;
import bot.telegram.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@AllArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/images/{id}")
    public ResponseEntity<?> getImageById(@PathVariable("id") int id) {
        Image image = imageRepository.findById(id).orElse(null);
        assert image != null;
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(image.getBytes().length)
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }
}
