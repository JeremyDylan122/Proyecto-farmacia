package com.msclientebeneficio.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Deshabilitado para evitar agotar las conexiones a la base de datos de Supabase durante la compilación local")
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}

