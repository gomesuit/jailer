package jailer;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationStarter {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStarter.class, args);
	}
	
	@Bean
	public ZooKeeper zooKeeper() throws IOException{
		return new ZooKeeper("192.168.33.11:2181", 3000, null);
	}
}
