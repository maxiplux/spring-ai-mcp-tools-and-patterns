package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    Page<Product> findAllProducts(Pageable pageable);

    Product findProductById(Long id);

    Product findProductBySku(String sku);

    Page<Product> findProductsByCategory(Long categoryId, Pageable pageable);

    Page<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findProductsByNameContaining(String keyword, Pageable pageable);

    Product saveProduct(Product product);

    Product updateProduct(Long id, Product productDetails);

    void deleteProduct(Long id);

    boolean existsById(Long id);
} 