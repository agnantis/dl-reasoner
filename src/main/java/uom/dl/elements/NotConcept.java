package uom.dl.elements;

import uom.dl.utils.NNFFactory;


public class NotConcept implements Concept {
	
	private final Concept concept;
	private final boolean isNNF;
	
	public NotConcept(Concept concept){
		this.concept = concept;
		if (concept instanceof AtomicConcept)
			this.isNNF = true;
		else
			this.isNNF = false;
	}
	
	@Override
	public Concept getConceptA() {
		return this.concept;
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

}
