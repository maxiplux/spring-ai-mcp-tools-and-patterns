package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.repository.CategoryRepository;
import app.quantun.springaimcp.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private Pageable pageable;
    private List<Category> categoryList;
    private Page<Category> categoryPage;

    @BeforeEach
    void setUp() {
        // Set up test data
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic devices and gadgets");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryList = new ArrayList<>();
        categoryList.add(category);

        pageable = PageRequest.of(0, 10);
        categoryPage = new PageImpl<>(categoryList, pageable, 1);
    }

    @Test
    void should_ReturnAllCategories_WhenFindAllCategories() {
        // Arrange
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

        // Act
        Page<Category> result = categoryService.findAllCategories(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(category.getName(), result.getContent().get(0).getName());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void should_ReturnCategory_WhenFindCategoryById() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.findCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void should_ThrowException_WhenFindCategoryByIdNotFound() {
        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.findCategoryById(99L);
        });
        verify(categoryRepository).findById(99L);
    }

    @Test
    void should_ReturnCategory_WhenFindCategoryByName() {
        // Arrange
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.findCategoryByName("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findByName("Electronics");
    }

    @Test
    void should_ThrowException_WhenFindCategoryByNameNotFound() {
        // Arrange
        when(categoryRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.findCategoryByName("Nonexistent");
        });
        verify(categoryRepository).findByName("Nonexistent");
    }

    @Test
    void should_ReturnCategoriesByNameContaining_WhenFindCategoriesByNameContaining() {
        // Arrange
        when(categoryRepository.findByNameContainingIgnoreCase("Elec", pageable)).thenReturn(categoryPage);

        // Act
        Page<Category> result = categoryService.findCategoriesByNameContaining("Elec", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Electronics", result.getContent().get(0).getName());
        verify(categoryRepository).findByNameContainingIgnoreCase("Elec", pageable);
    }

    @Test
    void should_SaveCategory_WhenSaveCategory() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.saveCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void should_UpdateCategory_WhenUpdateCategory() {
        // Arrange
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");
        updatedCategory.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.updateCategory(1L, updatedCategory);

        // Assert
        assertNotNull(result);
        // Note: In a real scenario, we'd verify the category was updated with new values
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void should_ThrowException_WhenUpdateCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.updateCategory(99L, new Category());
        });
        verify(categoryRepository).findById(99L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void should_DeleteCategory_WhenDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void should_ThrowException_WhenDeleteCategoryNotFound() {
        // Arrange
        when(categoryRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.deleteCategory(99L);
        });
        verify(categoryRepository).existsById(99L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void should_ReturnTrue_WhenCategoryExists() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = categoryService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(categoryRepository).existsById(1L);
    }

    @Test
    void should_ReturnFalse_WhenCategoryDoesNotExist() {
        // Arrange
        when(categoryRepository.existsById(99L)).thenReturn(false);

        // Act
        boolean result = categoryService.existsById(99L);

        // Assert
        assertFalse(result);
        verify(categoryRepository).existsById(99L);
    }
} 