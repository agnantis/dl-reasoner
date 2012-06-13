package uom.dl.elements;

public class AtomicRole implements Role {
	private String name;

	public AtomicRole(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtomicRole))
			return false;
		
		return this.name.equals(((AtomicRole)obj).getName());
	}

}
