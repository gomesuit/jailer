package jailer.core;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

class DefaultWatcher implements Watcher{

	@Override
	public void process(WatchedEvent event) {
		System.out.println("DefaultWatcher.process!");
	}
}
