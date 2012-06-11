package uom.dl.elements;

public class ForAllConcept implements Concept {
	private Role role;
	private Concept concept;

	public ForAllConcept(Role role, Concept concept) {
		this.role = role;
		this.concept = concept;
	}
	
	public ForAllConcept(Role role) {
		this(role, Constants.TOP_CONCEPT);
	}
	
	
	@Override
	public String toString() {
		//(∀R.C)
		return "(∀" + this.role + "." + this.concept + ")";
	}

}
