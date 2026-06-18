package com.compra.farma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FarmaApplication {

	public static void main(String[] args) {
		System.out.println("Executing database cleanup before starting FarmaApplication...");
		try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
				"jdbc:postgresql://aws-1-us-west-1.pooler.supabase.com:6543/postgres?prepareThreshold=0",
				"postgres.wdymywqplyxxfxvngweg",
				"Cristebyjona")) {
			try (java.sql.Statement stmt = conn.createStatement()) {
				stmt.execute("DROP SCHEMA IF EXISTS compras CASCADE");
				stmt.execute("DROP TABLE IF EXISTS public.compra CASCADE");
				System.out.println("Cleaned up database schemas successfully!");
			}
		} catch (Exception e) {
			System.err.println("Database cleanup warning (could be expected if DB is offline): " + e.getMessage());
		}

		SpringApplication.run(FarmaApplication.class, args);
	}

}
