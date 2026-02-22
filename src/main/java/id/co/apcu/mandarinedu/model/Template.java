package id.co.apcu.mandarinedu.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "templates")
@Data
public class Template {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String category;

    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String fabricData;

    private Integer pageCount = 1;

    private Boolean isPublic = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}
