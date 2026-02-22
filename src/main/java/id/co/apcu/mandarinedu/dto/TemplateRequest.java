package id.co.apcu.mandarinedu.dto;

import lombok.Data;

@Data
public class TemplateRequest {
    private String name;
    private String category;
    private String thumbnailUrl;
    private String fabricData;
    private Integer pageCount;
    private Boolean isPublic;
}
