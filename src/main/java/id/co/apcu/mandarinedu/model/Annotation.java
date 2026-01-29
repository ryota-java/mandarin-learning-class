package id.co.apcu.mandarinedu.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private int pageNumber;

    @Column(columnDefinition = "TEXT")
    private String fabricData;
}
