package uom.dl.elements;

public class AtMostConcept implements Concept {
	private int number;
	private Concept concept;
	private Role role;

	public AtMostConcept(int number, Role role, Concept concept) {
		this.number = number;
		this.concept = concept;
		this.role = role;
	}
	
	public AtMostConcept(int number, Role role) {
		this(number, role, Constants.TOP_CONCEPT);
	}
	
	@Override
	public String toString() {
		return "" + Constants.ATMOST_CHAR + this.number + this.role + "." + this.concept;
	}
}
