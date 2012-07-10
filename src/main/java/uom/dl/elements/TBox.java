package uom.dl.elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.utils.ConceptFactory;

public class TBox {
	private static final Logger log = LoggerFactory.getLogger(TBox.class);
	private static int counter = 0;
	
	private Map<AtomicConcept, Concept> equivConcepts = new HashMap<>();
	//private Map<AtomicConcept, List<Concept>> subConcepts = new HashMap<>();
	
	//SEARCH: check if knowing this:
	// A = B
	// A = C
	// we can transform it to this:
	// A = B and C
	public void addEquivalence(AtomicConcept c, Concept d) {
		Concept equivs = equivConcepts.get(c);
		if (equivs == null) {
			equivConcepts.put(c, d);			
		} else {
			equivConcepts.put(c, new IntersectionConcept(equivs, d));
		}
		
	}
	
	public void addSubsumer(AtomicConcept c, Concept d) {
		Concept cComp = new IntersectionConcept(new AtomicConcept(getRandomName(c)), d);
		addEquivalence(c, cComp);
	}
	
	
	public boolean isTBoxCyclicInDepth() {
		if (isTBoxCyclic())
			return true;
		//now we know that the tbox does not contains direct cycles
		//like: A = B AND A
		for (AtomicConcept ac : equivConcepts.keySet()) {
			if (isCyclicInDepth(ac, new HashSet<AtomicConcept>(0)))
				return true;
		}
		return false;
	}
	
	public boolean isCyclicInDepth(AtomicConcept ac, Set<AtomicConcept> visited) {
		Concept concept = equivConcepts.get(ac);
		if (concept == null)
			return false;
		
		visited.add(ac);
		Set<AtomicConcept> atomList = ConceptFactory.getAllAtomicConcepts(concept);
		
		for (AtomicConcept ac1 : atomList) {
			if (visited.contains(ac1)) {
				log.debug("Cycle found: " + visited + " -> " + ac1);
				return true;
			}
			if (isCyclicInDepth(ac1, visited))
				return true;
		}		
		return false;
	}
	
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
		Concept c = equivConcepts.get(ac);
		if (c == null)
			return false;
		
		boolean contains = ConceptFactory.contains(c, ac);
		if (contains)
			log.debug("Cycle found: " + ac + " -> " + c);
		return contains;
	}

	private static String getRandomName(AtomicConcept c) {
		++counter;
		return "$" + c.getName() + "_" + counter;
	}
	
	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept C = new AtomicConcept("C");
		AtomicConcept D = new AtomicConcept("D");
		
		TBox tbox = new TBox();
		tbox.addEquivalence(A, new UnionConcept(B, C));
		tbox.addEquivalence(B, D);
		tbox.addEquivalence(C, new IntersectionConcept(B, D));
		tbox.addEquivalence(D, new IntersectionConcept(B, D));
		
		boolean isSwallowCyclic = tbox.isTBoxCyclic();
		boolean isDeepCyclic = tbox.isTBoxCyclicInDepth();
		
		System.out.println("Is swallow cyclic: " + isSwallowCyclic);
		System.out.println("Is deep cyclic: " + isDeepCyclic);
		
		
	}
}
