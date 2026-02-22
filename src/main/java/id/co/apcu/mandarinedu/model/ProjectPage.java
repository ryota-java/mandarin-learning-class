package id.co.apcu.mandarinedu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "project_pages")
@Data
public class ProjectPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(name = "fabric_data", columnDefinition = "TEXT")
    private String fabricData;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
}
