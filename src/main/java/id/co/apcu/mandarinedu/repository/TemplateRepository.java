package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, String> {
    List<Template> findByCategory(String category);

    List<Template> findByIsPublicTrue();

    List<Template> findByCategoryAndIsPublicTrue(String category);

    List<Template> findByNameContainingIgnoreCase(String name);
}
