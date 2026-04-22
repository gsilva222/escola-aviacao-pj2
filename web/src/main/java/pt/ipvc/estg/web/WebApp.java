package pt.ipvc.estg.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Web (API REST)
 */
@SpringBootApplication
public class WebApp {
    
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }
}
