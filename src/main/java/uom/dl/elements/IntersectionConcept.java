package uom.dl.elements;

import uom.dl.utils.NNFFactory;

public class IntersectionConcept implements BinaryConcept {
	private Concept concept1;
	private Concept concept2;
	private boolean isNNF;

	public IntersectionConcept(Concept c, Concept d) {
		this.concept1 = c;
		this.concept2 = d;
		this.isNNF = c.isNNF() && d.isNNF();
	}

	@Override
	public Concept getConceptA() {
		return this.concept1;
	}

	@Override
	public Concept getConceptB() {
		return this.concept2;
	}
	
	@Override
	public String toString() {
		//return "(" + this.concept1 + Constants.INTERSECTION_CHAR + this.concept2 + ")";
		StringBuffer sb = new StringBuffer();
		if (this.concept1 instanceof BinaryConcept)
			sb.append("(" + this.concept1 + ")");
		else
			sb.append(this.concept1);
		sb.append(Constants.INTERSECTION_CHAR);
		if (this.concept2 instanceof BinaryConcept)
			sb.append("(" + this.concept2 + ")");
		else
			sb.append(this.concept2);
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return concept1.hashCode() + concept2.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntersectionConcept))
			return false;
		
		return this.getConceptA().equals(((IntersectionConcept)obj).getConceptA()) 
				&& this.getConceptB().equals(((IntersectionConcept)obj).getConceptB());
	}

	@Override
	public boolean isNNF() {
		return this.isNNF ;
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
