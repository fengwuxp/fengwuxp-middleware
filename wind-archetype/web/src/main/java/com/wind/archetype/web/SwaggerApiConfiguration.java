package com.wind.archetype.web;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wind
 * @date 2023-09-26 14:24
 **/
@Configuration
@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
public class SwaggerApiConfiguration {

    @Bean
    public OpenAPI swaggerOpenApi3() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes("basicScheme",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("basic")
                                )
                ).info(
                        new Info()
                                .title("Example API")
                                .version("1.0.0")
                );
    }

}