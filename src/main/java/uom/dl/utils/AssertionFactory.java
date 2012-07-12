package uom.dl.utils;

import java.util.HashMap;
import java.util.HashSet;
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
			assertionSet.add(new ConceptAssertion(c, ind));
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
		return new ConceptAssertion(concept, ind);		
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
			assertionSet.add(new ConceptAssertion(c, ind));
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
		return new ConceptAssertion(concept, ind);		
	}

}
