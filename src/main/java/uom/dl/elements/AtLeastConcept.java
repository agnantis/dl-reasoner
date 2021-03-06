package uom.dl.elements;

import uom.dl.utils.NNFFactory;

public class AtLeastConcept implements Concept {
	private final int number;
	private Concept concept;
	private final Role role;
	private boolean isNNF;

	public AtLeastConcept(int number, Role role, Concept concept) {
		this.number = number;
		this.role = role;
		setConceptA(concept);
	}
	
	public AtLeastConcept(int number, Role role) {
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
	public void setConceptA(Concept c) {
		this.concept = c;
		this.isNNF = concept.isNNF();
	}
	
	@Override
	public int hashCode() {
		return number + role.hashCode() + concept.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtLeastConcept))
			return false;
		
		AtLeastConcept other = (AtLeastConcept) obj;
		return (this.getCardinality() == other.getCardinality())
				&& this.getRole().equals(other.getRole())
				&& this.getConceptA().equals(other.getConceptA());
	}

	@Override
	public String toString() {
		if (this.concept == Constants.TOP_CONCEPT)
			return "" + Constants.ATLEAST_CHAR + this.number + this.role;
		if (this.concept instanceof BinaryConcept)
			return "" + Constants.ATLEAST_CHAR + this.number + this.role + ".(" + this.concept + ")";
		return "" + Constants.ATLEAST_CHAR + this.number + this.role + "." + this.concept;
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

	//<=1RT complement to >=xRT. x>1
	@Override
	public boolean isComplement(DLElement other) {
		if (!(other instanceof AtLeastConcept))
			return false;
		AtLeastConcept that = (AtLeastConcept) other;
		if (this.getRole().equals(that.getRole()) && this.getConceptA().equals(that.getConceptA())) {
			return this.getCardinality() > that.getCardinality();
		}
		return false;
	}

	@Override
	public boolean canHaveComplement() {
		return true;
	}
}
