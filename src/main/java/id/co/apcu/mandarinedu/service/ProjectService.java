package id.co.apcu.mandarinedu.service;

import id.co.apcu.mandarinedu.model.Project;
import id.co.apcu.mandarinedu.model.ProjectPage;
import id.co.apcu.mandarinedu.repository.ProjectPageRepository;
import id.co.apcu.mandarinedu.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectPageRepository pageRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByUpdatedAtDesc();
    }

    public List<Project> getProjectsByUser(String userId) {
        return projectRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    public Project createProject(String name, String userId) {
        Project project = new Project();
        project.setName(name);
        project.setUserId(userId != null ? userId : "anonymous");

        // Create first page
        ProjectPage firstPage = new ProjectPage();
        firstPage.setProject(project);
        firstPage.setPageNumber(1);
        firstPage.setFabricData("{\"objects\":[]}");
        project.getPages().add(firstPage);

        return projectRepository.save(project);
    }

    @Transactional
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public void updateProjectPage(String projectId, Integer pageNumber, String fabricData) {
        Optional<ProjectPage> page = pageRepository.findByProjectIdAndPageNumber(projectId, pageNumber);
        if (page.isPresent()) {
            page.get().setFabricData(fabricData);
            pageRepository.save(page.get());
        } else {
            // Create new page if not exists
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isPresent()) {
                ProjectPage newPage = new ProjectPage();
                newPage.setProject(project.get());
                newPage.setPageNumber(pageNumber);
                newPage.setFabricData(fabricData);
                pageRepository.save(newPage);
            }
        }

        // Update project timestamp
        projectRepository.findById(projectId).ifPresent(p -> {
            projectRepository.save(p);
        });
    }

    public List<ProjectPage> getProjectPages(String projectId) {
        return pageRepository.findByProjectIdOrderByPageNumberAsc(projectId);
    }

    public ProjectPage addPage(String projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) return null;

        List<ProjectPage> existingPages = pageRepository.findByProjectIdOrderByPageNumberAsc(projectId);
        int nextPageNumber = existingPages.isEmpty() ? 1 : existingPages.size() + 1;

        ProjectPage newPage = new ProjectPage();
        newPage.setProject(project.get());
        newPage.setPageNumber(nextPageNumber);
        newPage.setFabricData("{\"objects\":[]}");

        return pageRepository.save(newPage);
    }

    @Transactional
    public void deletePage(String projectId, Integer pageNumber) {
        pageRepository.findByProjectIdAndPageNumber(projectId, pageNumber)
            .ifPresent(page -> pageRepository.delete(page));

        // Renumber remaining pages
        List<ProjectPage> remainingPages = pageRepository.findByProjectIdOrderByPageNumberAsc(projectId);
        int num = 1;
        for (ProjectPage p : remainingPages) {
            p.setPageNumber(num++);
            pageRepository.save(p);
        }
    }

    @Transactional
    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }

    public void updateProjectName(String projectId, String name) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.setName(name);
            projectRepository.save(project);
        });
    }

    public void updateProjectThumbnail(String projectId, String thumbnailUrl) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.setThumbnailUrl(thumbnailUrl);
            projectRepository.save(project);
        });
    }
}
