package com.farmacia.proy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerProveedor {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Farma API - Sistema de Proveedores")
                        .version("1.0.0")
                        .description("API para Proveedor, Detalle y Factura"));
    }
}
