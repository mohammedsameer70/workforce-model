package com.boostphysioclinic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI workForceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WorkForce AI API")
                        .description("Workforce Demand Forecasting and Management API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Boost Physio Clinic")
                                .email("contact@boostphysioclinic.com")));
    }
}
