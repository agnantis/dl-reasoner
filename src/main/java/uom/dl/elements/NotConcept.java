package uom.dl.elements;

import uom.dl.utils.NNFFactory;


public class NotConcept implements Concept {
	private Concept concept;
	private boolean isNNF;
	
	public NotConcept(Concept concept){
		setConceptA(concept);
	}
	
	@Override
	public Concept getConceptA() {
		return this.concept;
	}
	
	@Override
	public void setConceptA(Concept c) {
		this.concept = c;
		this.isNNF = (concept instanceof AtomicConcept);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (this.concept instanceof BinaryConcept)
			return "" + Constants.NOT_CHAR +  "(" + this.concept + ")"; 
		return "" + Constants.NOT_CHAR + this.concept;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotConcept))
			return false;
		
		return this.getConceptA().equals(((NotConcept)obj).getConceptA());
	}
	
	@Override
	public int hashCode() {
		return this.concept.hashCode();
	}

	@Override
	public boolean isNNF() {
		return this.isNNF;
	}

	@Override
	public Concept toNNF() {
		if (isNNF())
			return this;
		return NNFFactory.getNNF(this.concept);
	}

	@Override
	public boolean isNegation() {
		return true;
	}

	@Override
	public boolean isAtomic() {
		return this.concept.isAtomic();
	}
	
	@Override
	public boolean isComplement(DLElement other) {
		if (!(other instanceof AtomicConcept) || (!this.concept.isAtomic()))
			return false;
		AtomicConcept c = (AtomicConcept) other;
		return this.getConceptA().equals(c);				
	}
	
	@Override
	public boolean canHaveComplement() {
		return true;
	}
	
}
