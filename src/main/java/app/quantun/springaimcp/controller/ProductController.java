package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.service.ProductService;
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

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "API for product management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a paginated list of products")
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.findProductById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Returns a product by its SKU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> getProductBySku(
            @Parameter(description = "Product SKU", required = true) @PathVariable String sku) {
        try {
            return ResponseEntity.ok(productService.findProductBySku(sku));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Returns a paginated list of products by category ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Long categoryId,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        try {
            return ResponseEntity.ok(productService.findProductsByCategory(categoryId, pageable));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Returns a paginated list of products within a price range")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "400", description = "Invalid price range")
    })
    public ResponseEntity<Page<Product>> getProductsByPriceRange(
            @Parameter(description = "Minimum price", required = true) @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price", required = true) @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        try {
            return ResponseEntity.ok(productService.findProductsByPriceRange(minPrice, maxPrice, pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Returns a paginated list of products containing the search keyword")
    public ResponseEntity<Page<Product>> searchProducts(
            @Parameter(description = "Search keyword", required = true) @RequestParam String keyword,
            @Parameter(description = "Pagination information") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.findProductsByNameContaining(keyword, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a product", description = "Creates a new product")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data or referenced category not found")
    })
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Product details", required = true) @Valid @RequestBody Product product) {
        try {
            return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true) @Valid @RequestBody Product product) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 