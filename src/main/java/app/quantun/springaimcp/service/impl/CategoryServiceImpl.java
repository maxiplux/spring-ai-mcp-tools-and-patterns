package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.repository.CategoryRepository;
import app.quantun.springaimcp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Page<Category> findAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }
    
    @Override
    public Category findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Category not found with name: " + name));
    }
    
    @Override
    public Page<Category> findCategoriesByNameContaining(String keyword, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = findCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
} 