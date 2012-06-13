package uom.dl.elements;

public class AtomicRole implements Role {
	private String name;

	public AtomicRole(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}	

}
