package app.quantun.springaimcp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgentUtilImplTest {
    @Autowired
    private AgentUtilImpl agentUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Create a test table
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_users");
        jdbcTemplate.execute("CREATE TABLE test_users (id INT, name VARCHAR(50), active BOOLEAN, salary DECIMAL(10,2))");

        // Insert test data
        jdbcTemplate.update("INSERT INTO test_users VALUES (1, 'Alice', true, 75000.50)");
        jdbcTemplate.update("INSERT INTO test_users VALUES (2, 'Bob', true, 82500.75)");
        jdbcTemplate.update("INSERT INTO test_users VALUES (3, 'Charlie', false, 65000.25)");
    }

    @Test
    void testQueryToJsonStructure_WithoutParameters() {
        // Execute the query
        String sql = "SELECT * FROM test_users ORDER BY id";
        List<Map<String, Object>> result = agentUtil.queryToJsonStructure(sql);

        // Verify result size
        assertEquals(3, result.size(), "Should return 3 records");

        // Verify first record
        Map<String, Object> firstRow = result.get(0);
        assertEquals(1, firstRow.get("id"));
        assertEquals("Alice", firstRow.get("name"));
        assertEquals(true, firstRow.get("active"));
        assertEquals(new BigDecimal("75000.50"), firstRow.get("salary"));

        // Verify third record
        Map<String, Object> thirdRow = result.get(2);
        assertEquals(3, thirdRow.get("id"));
        assertEquals("Charlie", thirdRow.get("name"));
        assertEquals(false, thirdRow.get("active"));
        assertEquals(new BigDecimal("65000.25"), thirdRow.get("salary"));
    }

    @Test
    void testQueryToJsonStructure_WithParameters() {
        // Execute the query with parameters
        String sql = "SELECT * FROM test_users WHERE active = ? AND salary > ?";
        List<Map<String, Object>> result = agentUtil.queryToJsonStructure(sql, true, 70000);

        // Verify result
        assertEquals(2, result.size(), "Should return 2 active users with salary > 70000");

        // Verify all returned users are active and have salary > 70000
        for (Map<String, Object> row : result) {
            assertEquals(true, row.get("active"), "User should be active");
            assertTrue(((BigDecimal) row.get("salary")).compareTo(new BigDecimal("70000")) > 0,
                    "Salary should be greater than 70000");
        }
    }

    @Test
    void testQueryToJsonStructure_EmptyResult() {
        // Query that should return no results
        String sql = "SELECT * FROM test_users WHERE id > 100";
        List<Map<String, Object>> result = agentUtil.queryToJsonStructure(sql);

        // Verify result
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");
    }

    @Test
    void testQueryToJsonStructure_SpecificColumns() {
        // Query for specific columns
        String sql = "SELECT id, name FROM test_users WHERE id = ?";
        List<Map<String, Object>> result = agentUtil.queryToJsonStructure(sql, 2);

        // Verify result
        assertEquals(1, result.size(), "Should return 1 record");

        Map<String, Object> row = result.get(0);
        assertEquals(2, row.get("id"));
        assertEquals("Bob", row.get("name"));
        assertEquals(2, row.size(), "Should only have 2 columns");
        assertFalse(row.containsKey("active"), "Should not contain 'active' column");
        assertFalse(row.containsKey("salary"), "Should not contain 'salary' column");
    }


}
