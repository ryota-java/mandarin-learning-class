package id.co.apcu.mandarinedu.dto;

import lombok.Data;

@Data
public class AnnotationRequest {
    private String roomId;
    private int pageNumber;
    private String fabricData;
    private Boolean isCompressed;
}