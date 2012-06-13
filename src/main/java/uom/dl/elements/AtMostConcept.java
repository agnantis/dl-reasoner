package uom.dl.elements;

public class AtMostConcept implements Concept {
	private final int number;
	private final Concept concept;
	private final Role role;
	private boolean isNNF;

	public AtMostConcept(int number, Role role, Concept concept) {
		this.number = number;
		this.concept = concept;
		this.role = role;
	}
	
	public AtMostConcept(int number, Role role) {
		this(number, role, Constants.TOP_CONCEPT);
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public int getCardinality() {
		return this.number;
	}
	
	@Override
	public Concept getConceptA() {
		return this.concept;
	}

	@Override
	public String toString() {
		return "" + Constants.ATMOST_CHAR + this.number + this.role + "." + this.concept;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtMostConcept))
			return false;
		
		AtMostConcept other = (AtMostConcept) obj;
		return (this.getCardinality() == other.getCardinality())
				&& this.getRole().equals(other.getRole())
				&& this.getConceptA().equals(other.getConceptA());
	}

	@Override
	public boolean isNNF() {
		return this.isNNF;
	}

	@Override
	public void toNNF() {
		// TODO Auto-generated method stub
		
	}
	
}
