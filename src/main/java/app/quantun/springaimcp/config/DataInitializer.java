package app.quantun.springaimcp.config;

import app.quantun.springaimcp.model.entity.Category;
import app.quantun.springaimcp.model.entity.Product;
import app.quantun.springaimcp.model.entity.Role;
import app.quantun.springaimcp.model.entity.User;
import app.quantun.springaimcp.service.AgentCategoryService;
import app.quantun.springaimcp.service.ProductService;
import app.quantun.springaimcp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AgentCategoryService agentCategoryService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public DataInitializer(
            AgentCategoryService agentCategoryService,
            ProductService productService,
            UserService userService) {
        this.agentCategoryService = agentCategoryService;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        // Check if data already exists
        Pageable allRecords = PageRequest.of(0, Integer.MAX_VALUE);
        if (agentCategoryService.findAllCategories(allRecords).isEmpty() &&
                productService.findAllProducts(allRecords).isEmpty() &&
                !userService.existsByUsername("admin")) {
            loadData();
        }
    }

    private void loadData() {
        // Create categories
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices and gadgets");
        electronics = agentCategoryService.saveCategory(electronics);

        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Apparel and fashion items");
        clothing = agentCategoryService.saveCategory(clothing);

        Category books = new Category();
        books.setName("Books");
        books.setDescription("Books, e-books, and publications");
        books = agentCategoryService.saveCategory(books);

        // Create products
        Product smartphone = new Product();
        smartphone.setName("Smartphone X");
        smartphone.setDescription("Latest smartphone with advanced features");
        smartphone.setPrice(new BigDecimal("999.99"));
        smartphone.setSku("ELEC-001");
        smartphone.setCategory(electronics);
        productService.saveProduct(smartphone);

        Product laptop = new Product();
        laptop.setName("Laptop Pro");
        laptop.setDescription("High-performance laptop for professionals");
        laptop.setPrice(new BigDecimal("1499.99"));
        laptop.setSku("ELEC-002");
        laptop.setCategory(electronics);
        productService.saveProduct(laptop);

        Product tShirt = new Product();
        tShirt.setName("Classic T-Shirt");
        tShirt.setDescription("Comfortable cotton t-shirt");
        tShirt.setPrice(new BigDecimal("19.99"));
        tShirt.setSku("CLOTH-001");
        tShirt.setCategory(clothing);
        productService.saveProduct(tShirt);

        Product jeans = new Product();
        jeans.setName("Denim Jeans");
        jeans.setDescription("Stylish denim jeans for everyday wear");
        jeans.setPrice(new BigDecimal("49.99"));
        jeans.setSku("CLOTH-002");
        jeans.setCategory(clothing);
        productService.saveProduct(jeans);

        Product novel = new Product();
        novel.setName("Bestseller Novel");
        novel.setDescription("Award-winning fiction novel");
        novel.setPrice(new BigDecimal("24.99"));
        novel.setSku("BOOK-001");
        novel.setCategory(books);
        productService.saveProduct(novel);

        Product cookbook = new Product();
        cookbook.setName("Gourmet Cookbook");
        cookbook.setDescription("Collection of gourmet recipes");
        cookbook.setPrice(new BigDecimal("34.99"));
        cookbook.setSku("BOOK-002");
        cookbook.setCategory(books);
        productService.saveProduct(cookbook);

        // Create users
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123"); // Will be encoded by the service
        admin.addRole(Role.ADMIN);
        userService.registerUser(admin);

        User regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword("user123"); // Will be encoded by the service
        userService.registerUser(regularUser);
    }
} 