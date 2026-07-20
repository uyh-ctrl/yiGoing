package com.macro.mall.portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {
    @Bean
    OpenAPI mallPortalOpenApi() {
        return new OpenAPI().info(new Info().title("Mall 门户 API").description("商城前台接口").version("v1"));
    }
}
