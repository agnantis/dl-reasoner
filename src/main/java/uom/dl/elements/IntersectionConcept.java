package uom.dl.elements;

import java.util.Set;

import uom.dl.utils.ConceptFactory;
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
	
	public IntersectionConcept(Concept c1, Concept c2, Concept... concepts) {
		IntersectionConcept c = new IntersectionConcept(c1, c2);
		for (Concept concept : concepts) {
			c = new IntersectionConcept(c, concept);
		}
		this.concept1 = c.getConceptA();
		this.concept2 = c.getConceptB();
	}

	@Override
	public Concept getConceptA() {
		return this.concept1;
	}
	
	@Override
	public void setConceptA(Concept c) {
		this.concept1 = c;
		this.isNNF = this.concept1.isNNF() && this.concept2.isNNF();
	}

	@Override
	public Concept getConceptB() {
		return this.concept2;
	}
	
	@Override
	public void setConceptB(Concept c) {
		this.concept2 = c;
		this.isNNF = this.concept1.isNNF() && this.concept2.isNNF();
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
		
		Set<Concept> thisConcepts = ConceptFactory.getIntersectionConcepts(this);
		Set<Concept> otherConcepts = ConceptFactory.getIntersectionConcepts((Concept) obj);
		return thisConcepts.equals(otherConcepts);
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
