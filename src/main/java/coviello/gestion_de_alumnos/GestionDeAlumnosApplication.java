package coviello.gestion_de_alumnos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionDeAlumnosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeAlumnosApplication.class, args);
	}
}
