package id.co.apcu.mandarinedu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ClassroomState {
    @Id
    private String roomId;
    private Integer currentPage = 1;
    private boolean isBlurred = false;
    private boolean isAnnotationVisible = true;
}
