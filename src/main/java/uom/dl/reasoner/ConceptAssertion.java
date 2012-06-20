package uom.dl.reasoner;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;

public class ConceptAssertion implements Assertion {
	private final Concept concept;
	private final Individual ind;
	
	public ConceptAssertion(Concept concept, Individual ind) {
		this.concept = concept;
		this.ind = ind;
	}

	@Override
	public Concept getElement() {
		return concept;
	}

	@Override
	public Individual getIndividualA() {
		return ind;
	}

	@Override
	public boolean isAtomic() {
		return concept.isAtomic();
	}

	@Override
	public boolean isComplement(DLElement other) {
		if (other instanceof ConceptAssertion)
			return ((ConceptAssertion)other).getConcept().isComplement(this.getConcept());
		
		return false;
	}
	
	@Override
	public String toString() {
		if (this.getConcept().isAtomic()) {
			return this.getConcept() + "(" + this.getIndividual() + ")";	
		}
		return "(" + this.getConcept() + ")" + "(" + this.getIndividual() + ")";
	}

}