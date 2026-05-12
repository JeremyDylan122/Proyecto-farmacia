package com.boleta.gestionboleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionBoletaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionBoletaApplication.class, args);
    }
}
