package id.co.apcu.mandarinedu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_uploads")
@Data
public class UserUpload {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    private String filename;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "content_type")
    private String contentType;

    private Long size;

    @Column(name = "storage_url")
    private String storageUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}
