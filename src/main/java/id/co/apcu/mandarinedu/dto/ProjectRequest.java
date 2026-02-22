package id.co.apcu.mandarinedu.dto;

import lombok.Data;

@Data
public class ProjectRequest {
    private String name;
    private String userId;
    private Integer canvasWidth;
    private Integer canvasHeight;
}
