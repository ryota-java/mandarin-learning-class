package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByUserIdOrderByUpdatedAtDesc(String userId);

    List<Project> findAllByOrderByUpdatedAtDesc();
}
