package jailer;

import jailer.web.zookeeper.JailerZookeeperCurator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfiguration {
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
