package jailer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.zookeeper.ZooDefs.Ids.*;
import static org.apache.zookeeper.ZooDefs.Perms.*;

public class Test {

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.33.11:2181", 3000, null);
		//List<ACL> acls = new ArrayList<ACL>();
		//acls.add(new ACL(ALL, ANYONE_ID_UNSAFE));
		Hoge hoge = new Hoge(1, "test");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(hoge);
		
		zk.create("/tmp", json.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		byte strByte[] = zk.getData("/tmp", false, null);
		String result = new String(strByte, "UTF-8");
		Hoge hoge2 = mapper.readValue(result, Hoge.class);
		System.out.println(hoge2);
		
		zk.close();
	}

}
