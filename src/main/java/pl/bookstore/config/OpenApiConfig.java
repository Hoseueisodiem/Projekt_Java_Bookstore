package pl.bookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookstoreOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Bookstore API")
                .description("System zarzadzania ksiegarnia/wypozyczalnia ksiazek")
                .version("1.0.0"));
    }
}
