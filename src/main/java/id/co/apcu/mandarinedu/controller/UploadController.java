package id.co.apcu.mandarinedu.controller;

import id.co.apcu.mandarinedu.model.UserUpload;
import id.co.apcu.mandarinedu.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping
    public ResponseEntity<UserUpload> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            // Validate file type (images only for now)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(null);
            }

            // Limit file size (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(null);
            }

            UserUpload upload = uploadService.uploadFile(file, userId);
            System.out.println("✅ File uploaded: " + upload.getOriginalName());
            return ResponseEntity.status(HttpStatus.CREATED).body(upload);

        } catch (IOException e) {
            System.err.println("❌ Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserUpload>> getAllUploads(
            @RequestParam(value = "userId", required = false) String userId
    ) {
        try {
            List<UserUpload> uploads;
            if (userId != null && !userId.isEmpty()) {
                uploads = uploadService.getUploadsByUser(userId);
            } else {
                uploads = uploadService.getAllUploads();
            }
            return ResponseEntity.ok(uploads);
        } catch (Exception e) {
            System.err.println("❌ Error fetching uploads: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            byte[] content = uploadService.getFileContent(filename);
            if (content == null) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type from extension
            String contentType = "application/octet-stream";
            if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (filename.endsWith(".gif")) contentType = "image/gif";
            else if (filename.endsWith(".svg")) contentType = "image/svg+xml";
            else if (filename.endsWith(".webp")) contentType = "image/webp";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl(CacheControl.maxAge(java.time.Duration.ofDays(7)));

            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (IOException e) {
            System.err.println("❌ Error serving file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUpload(@PathVariable String id) {
        try {
            uploadService.deleteUpload(id);
            System.out.println("✅ Upload deleted: " + id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            System.err.println("❌ Error deleting upload: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
