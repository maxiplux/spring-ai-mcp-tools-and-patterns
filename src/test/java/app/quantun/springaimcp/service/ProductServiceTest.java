package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.repository.ProductRepository;
import app.quantun.springaimcp.service.impl.ProductServiceImpl;
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

import java.math.BigDecimal;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private Pageable pageable;
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

        pageable = PageRequest.of(0, 10);
        productPage = new PageImpl<>(productList, pageable, 1);
    }

    @Test
    void should_ReturnAllProducts_WhenFindAllProducts() {
        // Arrange
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findAllProducts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(product.getName(), result.getContent().get(0).getName());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void should_ReturnProduct_WhenFindProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void should_ThrowException_WhenFindProductByIdNotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.findProductById(99L);
        });
        verify(productRepository).findById(99L);
    }

    @Test
    void should_ReturnProduct_WhenFindProductBySku() {
        // Arrange
        when(productRepository.findBySku("TEST-SKU-123")).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findProductBySku("TEST-SKU-123");

        // Assert
        assertNotNull(result);
        assertEquals("TEST-SKU-123", result.getSku());
        verify(productRepository).findBySku("TEST-SKU-123");
    }

    @Test
    void should_ThrowException_WhenFindProductBySkuNotFound() {
        // Arrange
        when(productRepository.findBySku("INVALID-SKU")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.findProductBySku("INVALID-SKU");
        });
        verify(productRepository).findBySku("INVALID-SKU");
    }

    @Test
    void should_ReturnProductsByCategory_WhenFindProductsByCategory() {
        // Arrange
        when(categoryService.existsById(1L)).thenReturn(true);
        when(productRepository.findByCategoryId(1L, pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findProductsByCategory(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryService).existsById(1L);
        verify(productRepository).findByCategoryId(1L, pageable);
    }

    @Test
    void should_ThrowException_WhenFindProductsByCategoryInvalidCategory() {
        // Arrange
        when(categoryService.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.findProductsByCategory(99L, pageable);
        });
        verify(categoryService).existsById(99L);
        verifyNoInteractions(productRepository);
    }

    @Test
    void should_ReturnProductsByPriceRange_WhenFindProductsByPriceRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        when(productRepository.findByPriceBetween(minPrice, maxPrice, pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findProductsByPriceRange(minPrice, maxPrice, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Test
    void should_ThrowException_WhenFindProductsByPriceRangeInvalidRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("10.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.findProductsByPriceRange(minPrice, maxPrice, pageable);
        });
        verifyNoInteractions(productRepository);
    }

    @Test
    void should_ReturnProductsByNameContaining_WhenFindProductsByNameContaining() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.findProductsByNameContaining("Test", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByNameContainingIgnoreCase("Test", pageable);
    }

    @Test
    void should_SaveProduct_WhenSaveProduct() {
        // Arrange
        when(categoryService.findCategoryById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.saveProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(categoryService).findCategoryById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void should_UpdateProduct_WhenUpdateProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("199.99"));
        updatedProduct.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        // Note: In a real scenario, we'd verify the product was updated with new values
        verify(productRepository).findById(1L);
        verify(categoryService).findCategoryById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void should_ThrowException_WhenUpdateProductNotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.updateProduct(99L, new Product());
        });
        verify(productRepository).findById(99L);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    @Test
    void should_DeleteProduct_WhenDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void should_ThrowException_WhenDeleteProductNotFound() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.deleteProduct(99L);
        });
        verify(productRepository).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void should_ReturnTrue_WhenProductExists() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = productService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(productRepository).existsById(1L);
    }

    @Test
    void should_ReturnFalse_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act
        boolean result = productService.existsById(99L);

        // Assert
        assertFalse(result);
        verify(productRepository).existsById(99L);
    }
} 