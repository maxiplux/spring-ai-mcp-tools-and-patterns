package app.quantun.springaimcp.config.mcp;

import app.quantun.springaimcp.service.AgentCategoryService;
import app.quantun.springaimcp.service.AgentUtil;
import app.quantun.springaimcp.service.ProductService;
import app.quantun.springaimcp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class McpSever {

    private final AgentCategoryService agentCategoryService;
    private final ProductService productService;
    private final UserService userService;
    private final AgentUtil agentUtil;

    @Bean
    public ToolCallbackProvider tools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(this.agentCategoryService, this.productService, this.userService, this.agentUtil)
                .build();
    }


}
