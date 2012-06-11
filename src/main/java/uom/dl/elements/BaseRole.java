package uom.dl.elements;

public class BaseRole implements Role {
	private String name;

	public BaseRole(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}	

}
