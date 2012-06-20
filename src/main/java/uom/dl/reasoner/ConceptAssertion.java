package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;

public class ConceptAssertion implements Assertion {
	private final Concept concept;
	private final Individual ind;
	
	public ConceptAssertion(Concept concept, Individual ind) {
		this.concept = concept;
		this.ind = ind;
	}

	@Override
	public DLElement getElement() {
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
			return ((ConceptAssertion)other).getElement().isComplement(this.getElement());
		
		return false;
	}
	
	@Override
	public String toString() {
		if (this.getElement().isAtomic()) {
			return this.getElement() + "(" + this.getIndividualA() + ")";	
		}
		return "(" + this.getElement() + ")" + "(" + this.getIndividualA() + ")";
	}

	@Override
	public boolean executeRule(TTree<Assertion> model) {
		if (concept instanceof IntersectionConcept) {
			Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(concept);
			Set<Assertion> assertions = ConceptFactory.createAssertions(concepts, getIndividualA());
			model.append(new ArrayList<>(assertions), TTree.ADD_IN_SEQUENCE);
			return true;
		}
		if (concept instanceof UnionConcept) {
			Set<Concept> concepts = ConceptFactory.getUnionConcepts(concept);
			Set<Assertion> assertions = ConceptFactory.createAssertions(concepts, getIndividualA());
			model.append(new ArrayList<>(assertions), TTree.ADD_IN_PARALLEL);
		}
		
		return false;
	}

}
