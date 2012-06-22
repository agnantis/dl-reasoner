package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.AtMostConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;

public class ConceptAssertion implements Assertion {
	private final Concept concept;
	private Individual ind;
	
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
	public boolean isComplement(DLElement obj) {
		if (!(obj instanceof ConceptAssertion))
			return false;
		Assertion other = (Assertion) obj;
		return other.getIndividualA().equals(getIndividualA())
				&& other.getElement().isComplement(getElement());
	}
	
	@Override
	public String toString() {
		if (this.getElement().isAtomic()) {
			return this.getElement() + "(" + this.getIndividualA() + ")";	
		}
		return "(" + this.getElement() + ")" + "(" + this.getIndividualA() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConceptAssertion))
			return false;
		ConceptAssertion other = (ConceptAssertion) obj;
		return getIndividualA().equals(other.getIndividualA()) && getElement().equals(other);
		
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
			return true;
		}
		if (concept instanceof ForAllConcept) {
			ForAllConcept ec = (ForAllConcept) concept;
			Concept c = ec.getConceptA();
			Role role = ec.getRole();
			Map<TTree<Assertion>, List<Individual>> casesBeAdded = model.getUnspecifiedFiller(role, getIndividualA());
			for (TTree<Assertion> tree : casesBeAdded.keySet()) {
				List<Individual> theList = casesBeAdded.get(tree);
				List<Assertion> toBeAdded = new ArrayList<>(theList.size());
				for (Individual i : theList) {
					//add C(i), 
					toBeAdded.add(new ConceptAssertion(c, i));
				}			
				tree.append(toBeAdded, TTree.ADD_IN_SEQUENCE);
			}
			
			return true;
		}
		if (concept instanceof ExistsConcept) {
			ExistsConcept ec = (ExistsConcept) concept;
			Concept c = ec.getConceptA();
			Role role = ec.getRole();
			boolean containsInd = model.containsFiller(role, c, getIndividualA());
			if (!containsInd) {
				//create a random individual
				Individual newInd = new Individual();
				//add C(x), R(b,x)
				List<Assertion> toBeAdded = new ArrayList<>(2);
				toBeAdded.add(new ConceptAssertion(c, newInd));
				toBeAdded.add(new RoleAssertion(role, getIndividualA(), newInd));
				model.append(toBeAdded, TTree.ADD_IN_SEQUENCE);
				return true;
			}
		}
		if (concept instanceof AtMostConcept) {
			AtMostConcept amc = (AtMostConcept) concept;
			Concept c = amc.getConceptA();
			Role role = amc.getRole();
			int card = amc.getCardinality();
			//model.getFiller(role, c, ind);
		}
		
		return false;
	}

	@Override
	public void setIndividualA(Individual ind) {
		this.ind = ind;
	}

	@Override
	public Assertion getACopy() {
		ConceptAssertion ass = new ConceptAssertion(this.concept, new Individual(this.ind.getName()));
		return ass;
	}

}
