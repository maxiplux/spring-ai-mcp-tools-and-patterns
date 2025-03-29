package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;
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

        Pageable pageable = PageRequest.of(0, 20);
        categoryPage = new PageImpl<>(categoryList, pageable, 1);
    }

    @Test
    void should_ReturnAllCategories_WhenGetAllCategories() throws Exception {
        // Arrange
        when(categoryService.findAllCategories(any(Pageable.class))).thenReturn(categoryPage);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")))
                .andDo(print());

        verify(categoryService).findAllCategories(any(Pageable.class));
    }

    @Test
    void should_ReturnCategory_WhenGetCategoryById() throws Exception {
        // Arrange
        when(categoryService.findCategoryById(1L)).thenReturn(category);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andDo(print());

        verify(categoryService).findCategoryById(1L);
    }

    @Test
    void should_ReturnNotFound_WhenGetCategoryByIdNotFound() throws Exception {
        // Arrange
        when(categoryService.findCategoryById(99L)).thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(categoryService).findCategoryById(99L);
    }

    @Test
    void should_ReturnCategory_WhenGetCategoryByName() throws Exception {
        // Arrange
        when(categoryService.findCategoryByName("Electronics")).thenReturn(category);

        // Act & Assert
        mockMvc.perform(get("/api/categories/name/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andDo(print());

        verify(categoryService).findCategoryByName("Electronics");
    }

    @Test
    void should_ReturnNotFound_WhenGetCategoryByNameNotFound() throws Exception {
        // Arrange
        when(categoryService.findCategoryByName("Nonexistent")).thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/api/categories/name/Nonexistent"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(categoryService).findCategoryByName("Nonexistent");
    }

    @Test
    void should_ReturnCategoriesBySearch_WhenSearchCategories() throws Exception {
        // Arrange
        when(categoryService.findCategoriesByNameContaining(eq("Elec"), any(Pageable.class))).thenReturn(categoryPage);

        // Act & Assert
        mockMvc.perform(get("/api/categories/search")
                        .param("keyword", "Elec"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Electronics")))
                .andDo(print());

        verify(categoryService).findCategoriesByNameContaining(eq("Elec"), any(Pageable.class));
    }

    @Test
    void should_CreateCategory_WhenPostCategory() throws Exception {
        // Arrange
        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andDo(print());

        verify(categoryService).saveCategory(any(Category.class));
    }

    @Test
    void should_UpdateCategory_WhenPutCategory() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(category);

        // Act & Assert
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Electronics")))
                .andDo(print());

        verify(categoryService).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    void should_ReturnNotFound_WhenPutCategoryNotFound() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(99L), any(Category.class)))
                .thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(put("/api/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(categoryService).updateCategory(eq(99L), any(Category.class));
    }

    @Test
    void should_DeleteCategory_WhenDeleteCategory() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void should_ReturnNotFound_WhenDeleteCategoryNotFound() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Category not found")).when(categoryService).deleteCategory(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(categoryService).deleteCategory(99L);
    }
} 