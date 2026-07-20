package com.macro.mall.search.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2Config {
    @Bean
    OpenAPI mallSearchOpenApi() {
        return new OpenAPI().info(new Info().title("Mall 搜索 API").description("商品检索接口").version("v1"));
    }
}
