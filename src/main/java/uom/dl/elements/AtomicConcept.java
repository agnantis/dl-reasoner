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
	public void setConceptA(Concept c) {
		if (c instanceof AtomicConcept) {
			this.name = ((AtomicConcept)c).getName();
			return;
		} 
		
		throw new UnsupportedOperationException("You cannot change the concept of an AtomicConcept with a non-AtomicConcept: " + c.getClass().toString());
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
	@Override
	public boolean isComplement(DLElement other) {
		if (!(other instanceof NotConcept))
			return false;
		NotConcept c = (NotConcept) other;
		return this.equals(c.getConceptA());				
	}
	
	@Override
	public boolean canHaveComplement() {
		return true;
	}
	
	public static void main(String[] str) {
		AtomicConcept a = new AtomicConcept("A");
		System.out.println(a);
		a.setConceptA(new AtomicConcept("B"));
		System.out.println(a);
		a.setConceptA(new NotConcept(new AtomicConcept("B")));
		System.out.println(a);
	}
	
}
