package hackathon.kb.chakchak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChakchakApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChakchakApplication.class, args);
	}

}
