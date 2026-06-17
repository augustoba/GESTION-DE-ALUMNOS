package coviello.gestion_de_alumnos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GestionDeAlumnosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeAlumnosApplication.class, args);
	}
}
