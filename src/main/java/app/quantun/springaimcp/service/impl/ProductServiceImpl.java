package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.repository.ProductRepository;
import app.quantun.springaimcp.service.CategoryService;
import app.quantun.springaimcp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }
    
    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }
    
    @Override
    public Product findProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new NoSuchElementException("Product not found with SKU: " + sku));
    }
    
    @Override
    public Page<Product> findProductsByCategory(Long categoryId, Pageable pageable) {
        // Verify category exists
        if (!categoryService.existsById(categoryId)) {
            throw new NoSuchElementException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Override
    public Page<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }
    
    @Override
    public Page<Product> findProductsByNameContaining(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    @Override
    public Product saveProduct(Product product) {
        // Verify category exists if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryService.findCategoryById(product.getCategory().getId());
        }
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = findProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        
        // Only update category if provided
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            categoryService.findCategoryById(productDetails.getCategory().getId());
            product.setCategory(productDetails.getCategory());
        }
        
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
} 