package bot.telegram.utils;

import jakarta.servlet.http.Part;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

@Component
@Getter
public class ImageUpload {
    private static String PATH = "/home/aeldridg/images";
    private static File DIR;

    public static String upload(String url1) throws IOException {
        URL url = new URL(url1);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        String fileName = PATH + File.separator + "random_image";
        File file = new File(fileName);
        if (file.exists()) {
            fileName = addSuffix(file);
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();
        return fileName;
    }

    public static String upload(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null)
            return "";
        String fileName = PATH + File.separator + multipartFile.getOriginalFilename();
        File file = new File(fileName);
        if (file.exists()) {
            fileName = addSuffix(file);
        }
        multipartFile.transferTo(new File(fileName));
        return fileName;
    }

    public static void delete(String path) throws IOException {
        Files.deleteIfExists(new File(path).toPath());
    }

    private static String addSuffix(File file) {
        String res = file.getName();
        String MIME = ".png";
        if (res.endsWith(MIME))
            res = res.substring(0, res.indexOf(MIME));
        int i = 1;
        String suffix = "(" + i + ")";
        while (new File(PATH + File.separator + res + suffix + MIME).exists())
            suffix = "(" + ++i + ")";
        return PATH + File.separator + res + suffix + MIME;
    }
}