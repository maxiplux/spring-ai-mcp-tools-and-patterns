package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    Page<Category> findAllCategories(Pageable pageable);
    
    Category findCategoryById(Long id);
    
    Category findCategoryByName(String name);
    
    Page<Category> findCategoriesByNameContaining(String keyword, Pageable pageable);
    
    Category saveCategory(Category category);
    
    Category updateCategory(Long id, Category categoryDetails);
    
    void deleteCategory(Long id);
    
    boolean existsById(Long id);
} 