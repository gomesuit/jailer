package jailer.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class JansibleZookeeper {
	private final String host;
	private final int port;
	private final ZooKeeper zooKeeper;
	private static final Charset charset = StandardCharsets.UTF_8;
	
	public JansibleZookeeper(String host, int port) throws IOException{
		this(host, port, new DefaultWatcher());
	}
	
	public JansibleZookeeper(String host, int port, Watcher watcher) throws IOException{
		this.host = host;
		this.port = port;
		this.zooKeeper = new ZooKeeper(host + ":" + port, 3000, watcher);
	}

	public void createDataForPersistent(String path, String data) throws KeeperException, InterruptedException{
		zooKeeper.create(path, data.getBytes(charset), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	public String getData(String path) throws KeeperException, InterruptedException{
		byte[] strByte = zooKeeper.getData(path, false, null);
		return new String(strByte, charset);
	}
	
	public void setData(String path, String data) throws KeeperException, InterruptedException{
		zooKeeper.setData(path, data.getBytes(charset), -1);
	}
	
	public List<String> getChildren(String path) throws KeeperException, InterruptedException{
		return zooKeeper.getChildren(path, false);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void createDataForEphemeral(String path, String data) throws KeeperException, InterruptedException {
		zooKeeper.create(path, data.getBytes(charset), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	public String createDataForEphemeralSequential(String path, String data) throws KeeperException, InterruptedException {
		return zooKeeper.create(path, data.getBytes(charset), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}

	public void delete(String path) throws InterruptedException, KeeperException {
		zooKeeper.delete(path, -1);
	}

	public void exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
		zooKeeper.exists(path, watcher);
	}
}
