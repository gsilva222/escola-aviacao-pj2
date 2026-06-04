package pt.ipvc.estg.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe principal da aplicação Web (API REST)
 */
@SpringBootApplication
@EntityScan(basePackages = "pt.ipvc.estg.entities")
@EnableJpaRepositories(basePackages = "pt.ipvc.estg.web.repositories")
public class WebApp {
    
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }
}
