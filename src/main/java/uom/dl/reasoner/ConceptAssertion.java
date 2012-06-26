package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.AtMostConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.Individual.IndividualPair;
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
		return getIndividualA().equals(other.getIndividualA()) && getElement().equals(other.getElement());
		
	}

	@Override
	public List<TList<Assertion>> executeRule(TList<Assertion> model) {
		if (concept instanceof IntersectionConcept) {
			Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(concept);
			Set<Assertion> assertions = ConceptFactory.createAssertions(concepts, getIndividualA());
			List<Assertion> assList = new ArrayList<>(assertions);
			Collections.sort(assList, ConceptFactory.ASSERTION_COMPARATOR);
			model.append(assList);
			model = model.getNext();
			return Arrays.asList(model);
		}
		if (concept instanceof UnionConcept) {
			Set<Concept> concepts = ConceptFactory.getUnionConcepts(concept);
			Set<Assertion> assertions = ConceptFactory.createAssertions(concepts, getIndividualA());
			List<TList<Assertion>> newModels = new ArrayList<>(assertions.size());
			for (Assertion a : assertions) {
				TList<Assertion> newModel = TList.duplicate(model, false);
				newModel.append(Arrays.asList(a));
				newModel = newModel.getNext();
				newModels.add(newModel);
			}
			return newModels;
		}
		if (concept instanceof ForAllConcept) {
			ForAllConcept ec = (ForAllConcept) concept;
			Concept c = ec.getConceptA();
			Role role = ec.getRole();
			Set<Individual> casesBeAdded = model.getUnspecifiedFiller(role, getIndividualA());
			List<Assertion> toBeAdded = new ArrayList<>(casesBeAdded.size());
			for (Individual i : casesBeAdded) {
				//add C(i), 
				toBeAdded.add(new ConceptAssertion(c, i));
			}
			model.append(toBeAdded);
			model = model.getNext();
			return Arrays.asList(model);
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
				model.append(toBeAdded);
				model = model.getNext();
				return Arrays.asList(model);
			}
		}
		if (concept instanceof AtMostConcept) {
			AtMostConcept amc = (AtMostConcept) concept;
			Role role = amc.getRole();
			int card = amc.getCardinality();
			Set<Individual> allFillers = model.getFillers(role, ind);
			List<TList<Assertion>> newModels = new ArrayList<>(allFillers.size());
			if (allFillers.size() > card) {
				//find all possible couples
				List<IndividualPair> subPairs = Individual.getPairs(new ArrayList<>(allFillers));
				//create models for each substitution
				for (IndividualPair pair : subPairs) {
					TList<Assertion> newModel = TList.duplicate(model, true);
					newModel.substituteAssertions(pair.getFirst(), pair.getSecond());
					boolean validModel = TList.revalidateModel(newModel);
					if (validModel) {
						newModels.add(newModel);
					}					
				}	
				//do not move forward
				return newModels;
			} else {
				//move forward
				model = model.getNext();
				return Arrays.asList(model);
			}
			//do not move forward
		}		
		return new ArrayList<>(0);
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
