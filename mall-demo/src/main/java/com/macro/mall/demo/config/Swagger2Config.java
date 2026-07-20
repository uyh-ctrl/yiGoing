package com.macro.mall.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {
    @Bean
    OpenAPI mallDemoOpenApi() { return new OpenAPI().info(new Info().title("Mall Demo API").version("v1")); }
}
