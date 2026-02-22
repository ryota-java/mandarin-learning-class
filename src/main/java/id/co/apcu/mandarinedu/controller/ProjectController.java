package id.co.apcu.mandarinedu.controller;

import id.co.apcu.mandarinedu.dto.PageUpdateRequest;
import id.co.apcu.mandarinedu.dto.ProjectRequest;
import id.co.apcu.mandarinedu.model.Project;
import id.co.apcu.mandarinedu.model.ProjectPage;
import id.co.apcu.mandarinedu.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(
            @RequestParam(value = "userId", required = false) String userId
    ) {
        try {
            List<Project> projects;
            if (userId != null && !userId.isEmpty()) {
                projects = projectService.getProjectsByUser(userId);
            } else {
                projects = projectService.getAllProjects();
            }
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            System.err.println("❌ Error fetching projects: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        try {
            return projectService.getProjectById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("❌ Error fetching project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
        try {
            if (request.getName() == null || request.getName().isEmpty()) {
                request.setName("Untitled Project");
            }

            Project project = projectService.createProject(request.getName(), request.getUserId());

            if (request.getCanvasWidth() != null) {
                project.setCanvasWidth(request.getCanvasWidth());
            }
            if (request.getCanvasHeight() != null) {
                project.setCanvasHeight(request.getCanvasHeight());
            }

            project = projectService.saveProject(project);
            System.out.println("✅ Project created: " + project.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (Exception e) {
            System.err.println("❌ Error creating project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/pages")
    public ResponseEntity<List<ProjectPage>> getProjectPages(@PathVariable String id) {
        try {
            List<ProjectPage> pages = projectService.getProjectPages(id);
            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            System.err.println("❌ Error fetching pages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/pages")
    public ResponseEntity<String> updatePage(
            @PathVariable String id,
            @RequestBody PageUpdateRequest request
    ) {
        try {
            if (request.getPageNumber() == null || request.getPageNumber() < 1) {
                return ResponseEntity.badRequest().body("Invalid page number");
            }

            projectService.updateProjectPage(id, request.getPageNumber(), request.getFabricData());
            System.out.println("✅ Page updated: Project " + id + ", Page " + request.getPageNumber());
            return ResponseEntity.ok("Page updated");
        } catch (Exception e) {
            System.err.println("❌ Error updating page: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

    @PostMapping("/{id}/pages")
    public ResponseEntity<ProjectPage> addPage(@PathVariable String id) {
        try {
            ProjectPage page = projectService.addPage(id);
            if (page == null) {
                return ResponseEntity.notFound().build();
            }
            System.out.println("✅ Page added to project: " + id);
            return ResponseEntity.status(HttpStatus.CREATED).body(page);
        } catch (Exception e) {
            System.err.println("❌ Error adding page: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}/pages/{pageNumber}")
    public ResponseEntity<Void> deletePage(
            @PathVariable String id,
            @PathVariable Integer pageNumber
    ) {
        try {
            projectService.deletePage(id, pageNumber);
            System.out.println("✅ Page deleted: Project " + id + ", Page " + pageNumber);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting page: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<String> updateProjectName(
            @PathVariable String id,
            @RequestBody String name
    ) {
        try {
            projectService.updateProjectName(id, name.replace("\"", ""));
            return ResponseEntity.ok("Name updated");
        } catch (Exception e) {
            System.err.println("❌ Error updating name: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        try {
            projectService.deleteProject(id);
            System.out.println("✅ Project deleted: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error deleting project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
