package soat_fiap.siaes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SiaesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiaesApplication.class, args);
	}

}
