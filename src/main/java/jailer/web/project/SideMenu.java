package jailer.web.project;

public class SideMenu {
	private String url;
	private String name;
	private String pageName;
	
	public SideMenu(String url, String name, String pageName) {
		super();
		this.url = url;
		this.name = name;
		this.pageName = pageName;
	}
	
	public String getUrl() {
		return url;
	}
	public String getName() {
		return name;
	}
	public String getPageName() {
		return pageName;
	}
}
