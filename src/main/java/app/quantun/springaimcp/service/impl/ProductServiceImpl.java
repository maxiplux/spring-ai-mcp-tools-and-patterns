package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.repository.ProductRepository;
import app.quantun.springaimcp.service.CategoryService;
import app.quantun.springaimcp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    @Tool(description = "Find all products with pagination")
    public Page<Product> findAllProducts(@ToolParam(description = "Pagination settings") Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Tool(description = "Find product by ID")
    public Product findProductById(@ToolParam(description = "Product Id") Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    @Override
    @Tool(description = "Find product by SKU")
    public Product findProductBySku(@ToolParam(description = "Product sku") String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new NoSuchElementException("Product not found with SKU: " + sku));
    }

    @Override
    @Tool(description = "Find products by category ID with pagination")
    public Page<Product> findProductsByCategory(
            @ToolParam(description = "Category ID to filter by") Long categoryId, 
            @ToolParam(description = "Pagination settings") Pageable pageable) {
        // Verify category exists
        if (!categoryService.existsById(categoryId)) {
            throw new NoSuchElementException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    @Tool(description = "Find products within a specific price range")
    public Page<Product> findProductsByPriceRange(
            @ToolParam(description = "Minimum price (inclusive)") BigDecimal minPrice, 
            @ToolParam(description = "Maximum price (inclusive)") BigDecimal maxPrice, 
            @ToolParam(description = "Pagination settings") Pageable pageable) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    @Tool(description = "Search for products by name (case-insensitive)")
    public Page<Product> findProductsByNameContaining(
            @ToolParam(description = "Search keyword for product name") String keyword, 
            @ToolParam(description = "Pagination settings") Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    @Tool(description = "Create a new product")
    public Product saveProduct(
            @ToolParam(description = "Product object with details to save") Product product) {
        // Verify category exists if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            categoryService.findCategoryById(product.getCategory().getId());
        }
        return productRepository.save(product);
    }

    @Override
    @Tool(description = "Update an existing product")
    public Product updateProduct(
            @ToolParam(description = "ID of the product to update") Long id, 
            @ToolParam(description = "Product object with updated details") Product productDetails) {
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
    @Tool(description = "Delete a product by ID")
    public void deleteProduct(
            @ToolParam(description = "ID of the product to delete") Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Tool(description = "Check if a product exists by ID")
    public boolean existsById(
            @ToolParam(description = "ID of the product to check") Long id) {
        return productRepository.existsById(id);
    }
}
