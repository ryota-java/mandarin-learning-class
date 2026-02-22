package id.co.apcu.mandarinedu.repository;

import id.co.apcu.mandarinedu.model.TemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateCategoryRepository extends JpaRepository<TemplateCategory, Long> {
    List<TemplateCategory> findAllByOrderBySortOrderAsc();
}
