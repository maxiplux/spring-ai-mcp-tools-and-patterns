package app.quantun.springaimcp.repository;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    List<Product> findByCategory(Category category);

    Page<Product> findByCategory(Category category, Pageable pageable);

    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    <S extends Product> S save(S entity);
} 