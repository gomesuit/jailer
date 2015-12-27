package jailer;

import jailer.core.JailerZookeeperCurator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationStarter {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStarter.class, args);
	}
	
	@Bean
	public JailerZookeeperCurator JansibleZookeeper(){
		return new JailerZookeeperCurator("192.168.33.11:2181");
	}
}
