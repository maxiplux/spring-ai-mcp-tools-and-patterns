package app.quantun.springaimcp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring AI MCP Catalog API")
                        .description("Spring AI MCP catalog application API documentation")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Quantun")
                                .email("info@quantun.app")
                                .url("https://quantun.app"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
} 