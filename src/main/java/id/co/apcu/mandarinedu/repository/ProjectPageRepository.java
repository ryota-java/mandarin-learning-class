package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.ProjectPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectPageRepository extends JpaRepository<ProjectPage, Long> {
    List<ProjectPage> findByProjectIdOrderByPageNumberAsc(String projectId);

    Optional<ProjectPage> findByProjectIdAndPageNumber(String projectId, Integer pageNumber);

    void deleteByProjectId(String projectId);
}
