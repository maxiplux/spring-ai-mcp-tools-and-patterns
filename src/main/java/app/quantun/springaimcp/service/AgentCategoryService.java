package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Category;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgentCategoryService {


    List<Category> findAllCategories();


    Page<Category> findAllCategories(@ToolParam(description = "Pagination settings") Pageable pageable);


    Category findCategoryById(@ToolParam(description = "Category ID") Long id);


    Category findCategoryByName(@ToolParam(description = "Category name") String name);


    Page<Category> findCategoriesByNameContaining(
            String keyword,
            Pageable pageable);


    Category saveCategory(Category category);


    Category updateCategory(
            Long id,
            Category categoryDetails);


    void deleteCategory(Long id);


    boolean existsById(Long id);
}