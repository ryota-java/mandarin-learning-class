package id.co.apcu.mandarinedu.service;

import id.co.apcu.mandarinedu.model.UserUpload;
import id.co.apcu.mandarinedu.repository.UserUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadService {

    @Autowired
    private UserUploadRepository repository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public UserUpload uploadFile(MultipartFile file, String userId) throws IOException {
        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create database record
        UserUpload upload = new UserUpload();
        upload.setUserId(userId != null ? userId : "anonymous");
        upload.setFilename(uniqueFilename);
        upload.setOriginalName(originalFilename);
        upload.setContentType(file.getContentType());
        upload.setSize(file.getSize());
        upload.setStorageUrl("/api/uploads/files/" + uniqueFilename);

        return repository.save(upload);
    }

    public List<UserUpload> getAllUploads() {
        return repository.findAllByOrderByUploadedAtDesc();
    }

    public List<UserUpload> getUploadsByUser(String userId) {
        return repository.findByUserIdOrderByUploadedAtDesc(userId);
    }

    public Optional<UserUpload> getUploadById(String id) {
        return repository.findById(id);
    }

    public byte[] getFileContent(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        }
        return null;
    }

    public void deleteUpload(String id) throws IOException {
        Optional<UserUpload> upload = repository.findById(id);
        if (upload.isPresent()) {
            // Delete file from disk
            Path filePath = Paths.get(uploadDir).resolve(upload.get().getFilename());
            Files.deleteIfExists(filePath);

            // Delete database record
            repository.deleteById(id);
        }
    }
}
