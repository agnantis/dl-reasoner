package uom.dl.elements;

import uom.dl.utils.NNFFactory;

public class NotConcept implements Concept {
	
	private Concept concept;
	private boolean isNNF = false;
	
	public NotConcept(Concept concept){
		this.concept = concept;
	}
	
	@Override
	public Concept getConceptA() {
		return this.concept;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + Constants.NOT_CHAR + this.concept;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotConcept))
			return false;
		
		return this.getConceptA().equals(((NotConcept)obj).getConceptA());
	}

	@Override
	public boolean isNNF() {
		return this.isNNF;
	}

	@Override
	public void toNNF() {
		if (isNNF())
			return;
		
		this.concept = NNFFactory.getNNF(this.concept);
		this.isNNF = true;		
	}

}
