package com.inventario.msinventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Inventario")
                        .version("1.0.1")
                        .description("Microservicio encargado del control de stock global, administración de lotes bajo la estrategia FEFO y el registro de auditoría de movimientos de bodega. Se comunica con los módulos de compras y ventas.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("ang.riverag@duoc.cl")
                                .url("https://www.duoc.cl/")));
    }

}
