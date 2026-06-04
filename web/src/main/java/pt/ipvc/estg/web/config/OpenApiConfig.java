package pt.ipvc.estg.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI escolaAviacaoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Escola de Aviacao API")
                        .version("1.0")
                        .description("API REST para Escola de Aviacao")
                        .license(new License().name("Proprietary"))
                );
    }
}
