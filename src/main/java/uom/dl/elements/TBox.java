package uom.dl.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.utils.ConceptFactory;

public class TBox {
	private static int counter = 0;
	
	private Map<AtomicConcept, List<Concept>> equivConcepts = new HashMap<>();
	//private Map<AtomicConcept, List<Concept>> subConcepts = new HashMap<>();
	
	public void addEquivalence(AtomicConcept c, Concept d) {
		List<Concept> equivs = equivConcepts.get(c);
		if (equivs == null) {
			equivs = new ArrayList<>();
		}
		equivs.add(d);
		equivConcepts.put(c, equivs);
	}
	
	public void addSubsumer(AtomicConcept c, Concept d) {
		Concept cComp = new IntersectionConcept(new AtomicConcept(getRandomName(c)), d);
		addEquivalence(c, cComp);
	}
	
	/*
	public boolean isTBoxCyclicInDepth() {
		if (isTBoxCyclic())
			return true;
		//now we know that the tbox does not contains direct cycles
		//like: A = B AND A
		for (AtomicConcept ac : equivConcepts.keySet()) {
			Set<AtomicConcept> atomicList = new HashSet<>();
			List<Concept> cons = equivConcepts.get(ac);
			for (Concept c : cons) {
				atomicList = ConceptFactory.getAllAtomicConcepts(c);
			}
			if (atomicList.contains(ac))
				return true;
			for (AtomicConcept inC : atomicList) {
				List<Concept> newCL = equivConcepts.get(inC);
				
			}
		}
	}*/
	
	/**
	 * Checks if at least one of the named concepts, contains a cycle
	 * @return true if one named concept contains a cycle, otherwise false
	 */
	public boolean isTBoxCyclic() {
		for (AtomicConcept ac : equivConcepts.keySet()) {
			if (isConceptCyclic(ac))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the definition of the named concept ac contains a cycle
	 * @param ac a named concept 
	 * @return true if the named concept ac contains a cycle, otherwise false
	 */
	public boolean isConceptCyclic(AtomicConcept ac) {
		List<Concept> cons = equivConcepts.get(ac);
		if (cons == null)
			return false;
		for (Concept c : cons) {
			if (ConceptFactory.contains(c, ac))
				return true;
		}
		return false;
	}

	private static String getRandomName(AtomicConcept c) {
		++counter;
		return "$" + c.getName() + "_" + counter;
	}
}
