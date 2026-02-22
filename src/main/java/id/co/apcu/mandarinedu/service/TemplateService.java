package id.co.apcu.mandarinedu.service;

import id.co.apcu.mandarinedu.model.Template;
import id.co.apcu.mandarinedu.model.TemplateCategory;
import id.co.apcu.mandarinedu.repository.TemplateCategoryRepository;
import id.co.apcu.mandarinedu.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateCategoryRepository categoryRepository;

    // Template methods
    public List<Template> getAllPublicTemplates() {
        return templateRepository.findByIsPublicTrue();
    }

    public List<Template> getTemplatesByCategory(String category) {
        return templateRepository.findByCategoryAndIsPublicTrue(category);
    }

    public Optional<Template> getTemplateById(String id) {
        return templateRepository.findById(id);
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }

    public List<Template> searchTemplates(String query) {
        return templateRepository.findByNameContainingIgnoreCase(query);
    }

    // Category methods
    public List<TemplateCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public TemplateCategory saveCategory(TemplateCategory category) {
        return categoryRepository.save(category);
    }

    // Initialize default categories
    public void initDefaultCategories() {
        if (categoryRepository.count() == 0) {
            String[][] defaults = {
                {"Flashcard", "card", "1"},
                {"Vocabulary", "text", "2"},
                {"Dialogue", "message-circle", "3"},
                {"Grammar", "book", "4"},
                {"Stroke Order", "pen-tool", "5"},
                {"Blank", "file", "6"}
            };

            for (String[] cat : defaults) {
                TemplateCategory category = new TemplateCategory();
                category.setName(cat[0]);
                category.setIcon(cat[1]);
                category.setSortOrder(Integer.parseInt(cat[2]));
                categoryRepository.save(category);
            }
        }
    }
}
