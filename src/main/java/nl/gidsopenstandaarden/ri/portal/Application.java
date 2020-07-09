package nl.gidsopenstandaarden.ri.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 *
 */
@SpringBootApplication
@EntityScan("nl.gidsopenstandaarden.ri.portal.entity")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
