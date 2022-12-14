package com.example.auctionservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Auction-Service APIs")
                                .description("https://docs.google.com/document/d/1enEc_dVMxjiMx3OgHeewoWQUA3ED_5myLKQRHzklOps/edit")
                                .version("v0.0.1")
                                .license(new License().name("Apache 2.0")
                                                      .url("https://www.apache.org/licenses/LICENSE-2.0"))
                                .contact(new Contact().email("shiva.chandra11@gmail.com")))
                .externalDocs(new ExternalDocumentation().description("Github Repo")
                                                         .url("https://github.com/Shiva486/auction-service"));
    }
}
