package bot.telegram.controller;

import bot.telegram.models.Image;
import bot.telegram.repositories.ImageRepository;
import bot.telegram.utils.ImageUpload;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable("id") String idS) throws IOException {
        int id = 0;

        try {
            id = Integer.parseInt(idS);
        } catch (NumberFormatException ignored) {}
        Optional<Image> image = imageRepository.findById(id);
        byte[] arr = Files.readAllBytes(new File(image.isPresent() ? image.get().getPath() : ImageUpload.DEFAULT).toPath());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(arr);
    }

}
