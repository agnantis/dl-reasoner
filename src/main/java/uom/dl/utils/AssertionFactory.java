package uom.dl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ConceptAssertion;

public class AssertionFactory {
	
	public static Set<Assertion> mergeAssertionsAsUnions(Set<Assertion> assertions) {
		Map<Individual, Concept> indMap = new HashMap<>();
		//Group individuals
		for (Assertion a : assertions) {
			Concept concept = indMap.get(a.getIndividualA());
			if (concept == null) {
				concept = (Concept)a.getElement();
			} else {
				concept = new UnionConcept(concept, (Concept)a.getElement());
			}
			indMap.put(a.getIndividualA(), concept);
		}
		
		//Merge all
		Set<Assertion> assertionSet = new HashSet<>(indMap.size());
		for (Individual ind : indMap.keySet()) {
			Concept c = indMap.get(ind);
			assertionSet.add(new ConceptAssertion(c, ind, -1, new HashSet<Integer>()));
		}
		return assertionSet;
	}
	
	public static Assertion mergeAllAssertionsAsUnions(Set<Assertion> assertions, Individual ind) {
		Concept concept = null;
		for (Assertion a: assertions) {
			Concept c = (Concept)a.getElement();
			if (concept == null) {
				concept = c;
			} else {
				concept = new UnionConcept(concept, c);
			}			
		}
		if (concept == null)
			return null;
		return new ConceptAssertion(concept, ind, -1, new HashSet<Integer>());		
	}
	
	public static Set<Assertion> mergeAssertionsAsIntersections(Set<Assertion> assertions) {
		Map<Individual, Concept> indMap = new HashMap<>();
		//Group individuals
		for (Assertion a : assertions) {
			Concept concept = indMap.get(a.getIndividualA());
			if (concept == null) {
				concept = (Concept)a.getElement();
			} else {
				concept = new IntersectionConcept(concept, (Concept)a.getElement());
			}
			indMap.put(a.getIndividualA(), concept);
		}
		
		//Merge all
		Set<Assertion> assertionSet = new HashSet<>(indMap.size());
		for (Individual ind : indMap.keySet()) {
			Concept c = indMap.get(ind);
			assertionSet.add(new ConceptAssertion(c, ind, -1, new HashSet<Integer>()));
		}
		return assertionSet;
	}
	
	public static Assertion mergeAllAssertionsAsIntersections(Set<Assertion> assertions, Individual ind) {
		Concept concept = null;
		for (Assertion a: assertions) {
			Concept c = (Concept)a.getElement();
			if (concept == null) {
				concept = c;
			} else {
				concept = new IntersectionConcept(concept, c);
			}			
		}
		if (concept == null)
			return null;
		return new ConceptAssertion(concept, ind, -1, new HashSet<Integer>());		
	}
	
	public static List<Assertion> getAllUnionAssertions(Assertion assertion) {
		List<Assertion> aList = new ArrayList<>();
		if (assertion.getElement() instanceof UnionConcept) {
			UnionConcept uc = (UnionConcept) assertion.getElement(); 
			Assertion a1 = new ConceptAssertion(uc.getConceptA(), assertion.getIndividualA(), 
					assertion.getBranchFactor(), assertion.getDependencySet()); 
			Assertion a2 = new ConceptAssertion(uc.getConceptB(), assertion.getIndividualA(), 
					assertion.getBranchFactor(), assertion.getDependencySet()); 
			if (uc.getConceptA().isAtomic())
				aList.add(a1);
			else 
				aList.addAll(getAllUnionAssertions(a1));
			
			if (uc.getConceptB().isAtomic())
				aList.add(a2);
			else 
				aList.addAll(getAllUnionAssertions(a2));
		}
		return aList;
	}
	
	public static List<Assertion> getAllIntersectionAssertions(Assertion assertion) {
		List<Assertion> aList = new ArrayList<>();
		if (assertion.getElement() instanceof IntersectionConcept) {
			IntersectionConcept uc = (IntersectionConcept) assertion.getElement(); 
			Assertion a1 = new ConceptAssertion(uc.getConceptA(), assertion.getIndividualA(), 
					assertion.getBranchFactor(), assertion.getDependencySet()); 
			Assertion a2 = new ConceptAssertion(uc.getConceptB(), assertion.getIndividualA(), 
					assertion.getBranchFactor(), assertion.getDependencySet()); 
			if (uc.getConceptA().isAtomic())
				aList.add(a1);
			else 
				aList.addAll(getAllIntersectionAssertions(a1));
			
			if (uc.getConceptB().isAtomic())
				aList.add(a2);
			else 
				aList.addAll(getAllIntersectionAssertions(a2));
		}
		return aList;
	}

}
