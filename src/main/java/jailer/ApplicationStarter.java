package jailer;

import jailer.web.zookeeper.JailerZookeeperCurator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApplicationStarter {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationStarter.class, args);
	}

	@Bean
	public ZooKeeperProperties getZooKeeperProperties(){
		return new ZooKeeperProperties();
	}
	
	@Bean
	@Autowired
	public JailerZookeeperCurator JansibleZookeeper(ZooKeeperProperties zooKeeperProperties){
		return new JailerZookeeperCurator(zooKeeperProperties.getConnectionString());
	}
}
