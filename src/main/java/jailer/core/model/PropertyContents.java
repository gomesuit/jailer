package jailer.core.model;

public class PropertyContents {
	private String value;
	private boolean hide;
	
	public PropertyContents(){}
	
	public PropertyContents(String value, boolean hide) {
		super();
		this.value = value;
		this.hide = hide;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	@Override
	public String toString() {
		return "PropertyContents [value=" + value + ", hide=" + hide + "]";
	}
}
