package com.mynotes.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {
    Cloudinary cloudinary;

    public CloudinaryService() {
//        Map<String, String> valuesMap = new HashMap<>();
//        valuesMap.put("cloud_name", "drpo8twra");
//        valuesMap.put("api_key", "344384456334146");
//        valuesMap.put("api_secret", "O5PAjxh9Gdyc6IHkSyJpEQfidCs");
//        this.cloudinary = new Cloudinary(valuesMap);
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "drpo8twra",
                "api_key", "344384456334146",
                "api_secret", "O5PAjxh9Gdyc6IHkSyJpEQfidCs"
        ));
    }

    public Map upload(MultipartFile multipartFile) {
        try {
            File file = convert(multipartFile);
            Map result = cloudinary.uploader()
                                   .upload(file, ObjectUtils.emptyMap());
            if (!Files.deleteIfExists(file.toPath())) {
                throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
            }
            System.out.println("result = " + result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convert(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convFile;
    }

    public Map delete(String id) throws IOException {
        return cloudinary.uploader()
                         .destroy(id, ObjectUtils.emptyMap());
    }

}

