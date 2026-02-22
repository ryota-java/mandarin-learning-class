package id.co.apcu.mandarinedu.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "template_categories")
@Data
public class TemplateCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
