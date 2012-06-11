package uom.dl.elements;

public class ExistsConcept implements Concept {
	
	private Role role;
	private Concept concept;

	public ExistsConcept(Role role, Concept concept) {
		this.role = role;
		this.concept = concept;
	}
	
	public ExistsConcept(Role role) {
		this(role, Constants.TOP_CONCEPT);
	}
	
	
	@Override
	public String toString() {
		//(∃R.C)
		return "(∃" + this.role + "." + this.concept + ")";
	}

}
