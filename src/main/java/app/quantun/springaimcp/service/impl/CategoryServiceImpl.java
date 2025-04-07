package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.repository.CategoryRepository;
import app.quantun.springaimcp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Tool(description = "Find all categories")
    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Tool(description = "Find all categories with pagination")
    public Page<Category> findAllCategories(@ToolParam(description = "Pagination settings") Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Tool(description = "Find category by ID")
    public Category findCategoryById(@ToolParam(description = "Category ID") Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
    }

    @Override
    @Tool(description = "Find category by name (exact match)")
    public Category findCategoryByName(@ToolParam(description = "Category name") String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Category not found with name: " + name));
    }

    @Override
    @Tool(description = "Search for categories by name (case-insensitive)")
    public Page<Category> findCategoriesByNameContaining(
            @ToolParam(description = "Search keyword for category name") String keyword, 
            @ToolParam(description = "Pagination settings") Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    @Tool(description = "Create a new category")
    public Category saveCategory(@ToolParam(description = "Category object with details to save") Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Tool(description = "Update an existing category")
    public Category updateCategory(
            @ToolParam(description = "ID of the category to update") Long id, 
            @ToolParam(description = "Category object with updated details") Category categoryDetails) {
        Category category = findCategoryById(id);
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(category);
    }

    @Override
    @Tool(description = "Delete a category by ID")
    public void deleteCategory(@ToolParam(description = "ID of the category to delete") Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Tool(description = "Check if a category exists by ID")
    public boolean existsById(@ToolParam(description = "ID of the category to check") Long id) {
        return categoryRepository.existsById(id);
    }
}
