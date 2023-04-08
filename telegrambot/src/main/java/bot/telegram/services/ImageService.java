package bot.telegram.services;

import bot.telegram.models.Image;
import bot.telegram.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    @SneakyThrows
    public void deleteImage(int id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty())
            return;
        Files.deleteIfExists(new File(image.get().getPath()).toPath());
        imageRepository.deleteById(id);
    }

    @SneakyThrows
    public void deleteImage(Image image) {
        Files.deleteIfExists(new File(image.getPath()).toPath());
        imageRepository.deleteById(image.getId());
    }
}
