package uom.dl.elements;

public class AtLeastConcept implements Concept {
	private int number;
	private Concept concept;
	private Role role;

	public AtLeastConcept(int number, Role role, Concept concept) {
		this.number = number;
		this.role = role;
		this.concept = concept;
	}
	
	public AtLeastConcept(int number, Role role) {
		this(number, role, Constants.TOP_CONCEPT);
	}
	
	@Override
	public String toString() {
		return "" + Constants.ATLEAST_CHAR + this.number + this.role + "." + this.concept;
	}
}
