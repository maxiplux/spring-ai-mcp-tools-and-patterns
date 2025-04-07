package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.service.AgentUtil;
import jakarta.json.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the AgentUtil service interface that provides utilities for
 * working with JSON data and executing SQL queries with JSON results.
 * <p>
 * This service provides capabilities for:
 * <ul>
 *   <li>Parsing JSON strings into JsonStructure objects</li>
 *   <li>Converting JsonStructure objects to compact string representation</li>
 *   <li>Executing SQL queries and converting results to JSON format</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgentUtilImpl implements AgentUtil {


    private final JdbcTemplate jdbcTemplate;

    /**
     * Executes an SQL query and returns the results as a List of Maps.
     * Each Map represents a row with column names as keys and their values as objects.
     *
     * The method provides a convenient way to work with dynamic query results
     * without needing to define specific entity classes.
     *
     * @param sql The SQL query to execute
     * @param params Parameters for the SQL query (optional)
     * @return List<Map<String, Object>> containing the query results
     * @throws org.springframework.dao.DataAccessException if there is an error executing the query
     */
    @Tool(description = " Executes an SQL query and returns results as List<Map<String, Object>>")
    public List<Map<String, Object>> queryToJsonStructure(
            @ToolParam(description = "The SQL query to execute") String sql,
            @ToolParam(description = "Parameters for the SQL query (optional)") Object... params) {

        // Execute the query and return results directly as List<Map>
        return jdbcTemplate.queryForList(sql, params);
    }

    @Tool(description = "Execute a SQL query and return all tables in the database")
    public List<Map<String, Object>> getAllTables() {
        String sql = "SELECT     TABLE_NAME FROM      INFORMATION_SCHEMA.TABLES WHERE     TABLE_SCHEMA = 'PUBLIC'";
        return jdbcTemplate.queryForList(sql);
    }

    @Tool(description = "Get columns from a table")
    public List<Map<String, Object>> getColumnsFromTables(@ToolParam(description = "Table name") String tableName) {
        String sql = String.format("SELECT     COLUMN_NAME, data_type,COLUMN_DEFAULT as DEFAULT_VALUE,  ORDINAL_POSITION as POSITION FROM     INFORMATION_SCHEMA.COLUMNS WHERE     TABLE_SCHEMA = 'PUBLIC'     AND TABLE_NAME = '%s' ORDER BY     ORDINAL_POSITION;", tableName);
        return jdbcTemplate.queryForList(sql);
    }



}