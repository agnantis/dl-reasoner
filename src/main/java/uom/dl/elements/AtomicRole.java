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
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtomicRole))
			return false;
		
		return this.name.equals(((AtomicRole)obj).getName());
	}

	@Override
	public boolean isAtomic() {
		return true;
	}

	@Override
	public boolean isComplement(DLElement other) {
		if (other instanceof NotConcept) {
			Concept that = ((NotConcept)other).getConceptA();
			return this.equals(that);
		}			
		return false;
	}
	
	@Override
	public boolean canHaveComplement() {
		return true;
	}
	
}
