package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.service.ProductService;
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

import java.math.BigDecimal;
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
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private Category category;
    private List<Product> productList;
    private Page<Product> productPage;

    @BeforeEach
    void setUp() {
        // Set up test data
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setSku("TEST-SKU-123");
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productList = new ArrayList<>();
        productList.add(product);

        Pageable pageable = PageRequest.of(0, 20);
        productPage = new PageImpl<>(productList, pageable, 1);
    }

    @Test
    void should_ReturnAllProducts_WhenGetAllProducts() throws Exception {
        // Arrange
        when(productService.findAllProducts(any(Pageable.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andExpect(jsonPath("$.content[0].price", is(99.99)))
                .andDo(print());

        verify(productService).findAllProducts(any(Pageable.class));
    }

    @Test
    void should_ReturnProduct_WhenGetProductById() throws Exception {
        // Arrange
        when(productService.findProductById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andDo(print());

        verify(productService).findProductById(1L);
    }

    @Test
    void should_ReturnNotFound_WhenGetProductByIdNotFound() throws Exception {
        // Arrange
        when(productService.findProductById(99L)).thenThrow(new NoSuchElementException("Product not found"));

        // Act & Assert
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productService).findProductById(99L);
    }

    @Test
    void should_ReturnProduct_WhenGetProductBySku() throws Exception {
        // Arrange
        when(productService.findProductBySku("TEST-SKU-123")).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/sku/TEST-SKU-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku", is("TEST-SKU-123")))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andDo(print());

        verify(productService).findProductBySku("TEST-SKU-123");
    }

    @Test
    void should_ReturnNotFound_WhenGetProductBySkuNotFound() throws Exception {
        // Arrange
        when(productService.findProductBySku("INVALID-SKU")).thenThrow(new NoSuchElementException("Product not found"));

        // Act & Assert
        mockMvc.perform(get("/api/products/sku/INVALID-SKU"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productService).findProductBySku("INVALID-SKU");
    }

    @Test
    void should_ReturnProductsByCategory_WhenGetProductsByCategory() throws Exception {
        // Arrange
        when(productService.findProductsByCategory(eq(1L), any(Pageable.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andDo(print());

        verify(productService).findProductsByCategory(eq(1L), any(Pageable.class));
    }

    @Test
    void should_ReturnNotFound_WhenGetProductsByCategoryInvalidCategory() throws Exception {
        // Arrange
        when(productService.findProductsByCategory(eq(99L), any(Pageable.class)))
                .thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/api/products/category/99"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productService).findProductsByCategory(eq(99L), any(Pageable.class));
    }

    @Test
    void should_ReturnProductsByPriceRange_WhenGetProductsByPriceRange() throws Exception {
        // Arrange
        when(productService.findProductsByPriceRange(
                any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/api/products/price-range")
                        .param("minPrice", "10.00")
                        .param("maxPrice", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andDo(print());

        verify(productService).findProductsByPriceRange(
                any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class));
    }

    @Test
    void should_ReturnBadRequest_WhenGetProductsByPriceRangeInvalidRange() throws Exception {
        // Arrange
        when(productService.findProductsByPriceRange(
                any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Invalid price range"));

        // Act & Assert
        mockMvc.perform(get("/api/products/price-range")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "10.00"))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(productService).findProductsByPriceRange(
                any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class));
    }

    @Test
    void should_ReturnProductsBySearch_WhenSearchProducts() throws Exception {
        // Arrange
        when(productService.findProductsByNameContaining(eq("Test"), any(Pageable.class)))
                .thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andDo(print());

        verify(productService).findProductsByNameContaining(eq("Test"), any(Pageable.class));
    }

    @Test
    void should_CreateProduct_WhenPostProduct() throws Exception {
        // Arrange
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andDo(print());

        verify(productService).saveProduct(any(Product.class));
    }

    @Test
    void should_ReturnBadRequest_WhenPostProductInvalidCategory() throws Exception {
        // Arrange
        when(productService.saveProduct(any(Product.class)))
                .thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(productService).saveProduct(any(Product.class));
    }

    @Test
    void should_UpdateProduct_WhenPutProduct() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andDo(print());

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void should_ReturnNotFound_WhenPutProductNotFound() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(99L), any(Product.class)))
                .thenThrow(new NoSuchElementException("Product not found"));

        // Act & Assert
        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productService).updateProduct(eq(99L), any(Product.class));
    }

    @Test
    void should_DeleteProduct_WhenDeleteProduct() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void should_ReturnNotFound_WhenDeleteProductNotFound() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Product not found")).when(productService).deleteProduct(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(productService).deleteProduct(99L);
    }
} 