package bot.telegram.controller;

import bot.telegram.models.Image;
import bot.telegram.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@AllArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable("id") int id) throws IOException {
        Image image = imageRepository.findById(id).orElse(null);
        assert image != null;
        byte[] arr = Files.readAllBytes(new File(image.getPath()).toPath());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(arr);
    }

}
