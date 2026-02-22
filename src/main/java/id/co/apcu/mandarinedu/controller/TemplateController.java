package id.co.apcu.mandarinedu.controller;

import id.co.apcu.mandarinedu.dto.TemplateRequest;
import id.co.apcu.mandarinedu.model.Template;
import id.co.apcu.mandarinedu.model.TemplateCategory;
import id.co.apcu.mandarinedu.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates(
            @RequestParam(required = false) String category
    ) {
        try {
            List<Template> templates;
            if (category != null && !category.isEmpty()) {
                templates = templateService.getTemplatesByCategory(category);
            } else {
                templates = templateService.getAllPublicTemplates();
            }
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("❌ Error fetching templates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TemplateCategory>> getCategories() {
        try {
            List<TemplateCategory> categories = templateService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            System.err.println("❌ Error fetching categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(@PathVariable String id) {
        try {
            return templateService.getTemplateById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error fetching template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Template> createTemplate(@RequestBody TemplateRequest request) {
        try {
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            Template template = new Template();
            template.setName(request.getName());
            template.setCategory(request.getCategory());
            template.setThumbnailUrl(request.getThumbnailUrl());
            template.setFabricData(request.getFabricData());
            template.setPageCount(request.getPageCount() != null ? request.getPageCount() : 1);
            template.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

            Template saved = templateService.saveTemplate(template);
            System.out.println("✅ Template created: " + saved.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            System.err.println("❌ Error creating template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        try {
            templateService.deleteTemplate(id);
            System.out.println("✅ Template deleted: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Template>> searchTemplates(@RequestParam String q) {
        try {
            List<Template> templates = templateService.searchTemplates(q);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("❌ Error searching templates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/init-categories")
    public ResponseEntity<String> initCategories() {
        try {
            templateService.initDefaultCategories();
            return ResponseEntity.ok("Categories initialized");
        } catch (Exception e) {
            System.err.println("❌ Error initializing categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }
}
