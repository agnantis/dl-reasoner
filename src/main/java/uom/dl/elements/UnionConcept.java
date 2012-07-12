package uom.dl.elements;

import java.util.Set;

import uom.dl.utils.ConceptFactory;
import uom.dl.utils.NNFFactory;

public class UnionConcept implements BinaryConcept {
	private Concept concept1;
	private Concept concept2;
	private boolean isNNF;

	public UnionConcept(Concept c, Concept d) {
		this.concept1 = c;
		this.concept2 = d;
		this.isNNF = c.isNNF() && d.isNNF();
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
		StringBuffer sb = new StringBuffer();
		if (this.concept1 instanceof BinaryConcept)
			sb.append("(" + this.concept1 + ")");
		else
			sb.append(this.concept1);
		sb.append(Constants.UNION_CHAR);
		if (this.concept2 instanceof BinaryConcept)
			sb.append("(" + this.concept2 + ")");
		else
			sb.append(this.concept2);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UnionConcept))
			return false;
		Set<Concept> thisConcepts = ConceptFactory.getUnionConcepts(this);
		Set<Concept> otherConcepts = ConceptFactory.getUnionConcepts((Concept) obj);
		return thisConcepts.equals(otherConcepts);
	}
	
	@Override
	public int hashCode() {
		return concept1.hashCode() + concept2.hashCode();
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
