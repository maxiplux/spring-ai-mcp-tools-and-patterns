package app.quantun.springaimcp.service;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;
import java.util.Map;

public interface AgentUtil {
    @Tool(description = " Executes an SQL query and returns results as List<Map<String, Object>>")
    List<Map<String, Object>> queryToJsonStructure(
            @ToolParam(description = "The SQL query to execute") String sql,
            @ToolParam(description = "Parameters for the SQL query (optional)") Object... params);

    @Tool(description = "Execute a SQL query and return all tables in the database")
    List<Map<String, Object>> getAllTables();

    @Tool(description = "Get columns from a table")
    List<Map<String, Object>> getColumnsFromTales(@ToolParam(description = "Table name") String tableName);
}
