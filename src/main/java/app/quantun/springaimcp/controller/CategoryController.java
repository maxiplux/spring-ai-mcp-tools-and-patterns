package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "API for category management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns a paginated list of categories")
    public ResponseEntity<Page<Category>> getAllCategories(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoryService.findAllCategories(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a category by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.findCategoryById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get category by name", description = "Returns a category by its name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Category> getCategoryByName(
            @Parameter(description = "Category name", required = true) @PathVariable String name) {
        try {
            return ResponseEntity.ok(categoryService.findCategoryByName(name));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Returns categories containing the search keyword")
    public ResponseEntity<Page<Category>> searchCategories(
            @Parameter(description = "Search keyword", required = true) @RequestParam String keyword,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(categoryService.findCategoriesByNameContaining(keyword, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a category", description = "Creates a new category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    public ResponseEntity<Category> createCategory(
            @Parameter(description = "Category details", required = true) @Valid @RequestBody Category category) {
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Category> updateCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated category details", required = true) @Valid @RequestBody Category category) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, category));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 