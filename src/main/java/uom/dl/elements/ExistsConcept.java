package uom.dl.elements;

import uom.dl.utils.NNFFactory;

public class ExistsConcept implements Concept {
	private Role role;
	private Concept concept;
	private boolean isNNF;

	public ExistsConcept(Role role, Concept concept) {
		this.role = role;
		setConceptA(concept);
	}
	
	public ExistsConcept(Role role) {
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
	public void setConceptA(Concept c) {
		this.concept = c;
		this.isNNF = concept.isNNF();
	}
	
	@Override
	public int hashCode() {
		return role.hashCode() + concept.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExistsConcept))
			return false;
		
		ExistsConcept other = (ExistsConcept) obj;
		return this.getRole().equals(other.getRole())
				&& this.getConceptA().equals(other.getConceptA());
	}
	
	@Override
	public String toString() {
		//(∃R.C)
		if (this.concept instanceof BinaryConcept)
			return "" + Constants.EXISTS_CHAR + this.role + ".(" + this.concept + ")";
		return "" + Constants.EXISTS_CHAR + this.role + "." + this.concept;
	}

	@Override
	public boolean isNNF() {
		return this.isNNF;
	}

	@Override
	public Concept toNNF() {
		if (isNNF())
			return this;
		return NNFFactory.getNNF(this);		
	}

	@Override
	public boolean isNegation() {
		return false;
	}

	@Override
	public boolean isAtomic() {
		return false;
	}

	@Override
	public boolean isComplement(DLElement other) {
		return false;
	}
	
	@Override
	public boolean canHaveComplement() {
		return false;
	}
	
}
