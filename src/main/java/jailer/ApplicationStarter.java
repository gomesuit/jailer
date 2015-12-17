package jailer;

import java.io.IOException;

import jailer.core.JailerZookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationStarter {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStarter.class, args);
	}
	
	@Bean
	public JailerZookeeper JansibleZookeeper() throws IOException{
		return new JailerZookeeper("192.168.33.11", 2181);
	}
}
