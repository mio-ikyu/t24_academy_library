package jp.co.metateam.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("jp.co.metateam.library.repository")
public class MtLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtLibraryApplication.class, args);
	}

}
