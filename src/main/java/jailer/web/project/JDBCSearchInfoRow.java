package jailer.web.project;

import jailer.core.model.PropertyContents;

import java.util.HashMap;
import java.util.Map;


public class JDBCSearchInfoRow {
	private String group;
	private String id;
	private int point;
	private String driverName;
	private String url;
	private boolean hide;
	private Map<String, PropertyContents> propertyList = new HashMap<>();
	private String uuid;
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public Map<String, PropertyContents> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(Map<String, PropertyContents> propertyList) {
		this.propertyList = propertyList;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
