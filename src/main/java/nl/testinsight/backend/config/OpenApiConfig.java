package nl.testinsight.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI testInsightOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TestInsight API")
                        .description("API for importing and retrieving test results.")
                        .version("v1"));
    }
}
