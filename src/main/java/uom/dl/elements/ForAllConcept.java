package uom.dl.elements;

public class ForAllConcept implements Concept {
	private Role role;
	private Concept concept;
	private boolean isNNF;

	public ForAllConcept(Role role, Concept concept) {
		this.role = role;
		this.concept = concept;
	}
	
	public ForAllConcept(Role role) {
		this(role, Constants.TOP_CONCEPT);
	}
	
	public Role getRole() {
		return this.role;
	}
	
	@Override	
	public Concept getConceptA() {
		return this.concept;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ForAllConcept))
			return false;
		
		ForAllConcept other = (ForAllConcept) obj;
		return this.getRole().equals(other.getRole())
				&& this.getConceptA().equals(other.getConceptA());
	}
	
	@Override
	public String toString() {
		//(∀R.C)
		return "(" + Constants.FORALL_CHAR + this.role + "." + this.concept + ")";
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
