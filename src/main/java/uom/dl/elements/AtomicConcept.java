package uom.dl.elements;

public class AtomicConcept implements Concept {
	private String name;

	public AtomicConcept(char name) {
		this(name + "");
	}
	
	public AtomicConcept(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public Concept getConceptA() {
		return this;
	}

	@Override
	public String toString(){
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtomicConcept))
			return false;
		
		return this.name.equals(((AtomicConcept)obj).getName());
	}
	
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean isNNF() {
		return true;
	}

	@Override
	public Concept toNNF() {
		return this;		
	}

	@Override
	public boolean isNegation() {
		return false;
	}

	@Override
	public boolean isAtomic() {
		return true;
	}
}
