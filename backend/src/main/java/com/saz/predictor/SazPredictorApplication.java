package com.saz.predictor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Saz Note Predictor Backend-Anwendung.
 * Startet den Spring Boot Kontext.
 */
@SpringBootApplication
public class SazPredictorApplication {
	public static void main(String[] args) {
		SpringApplication.run(SazPredictorApplication.class, args);
	}
}
