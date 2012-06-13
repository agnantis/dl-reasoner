package uom.dl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.ConceptBuilder;
import uom.dl.elements.Constants;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;

public class ConceptFactory {
	
	public static Concept unionOfConcepts(Set<Concept> concepts) {
		if (concepts == null || concepts.isEmpty())
			return Constants.TOP_CONCEPT;
		
		Iterator<Concept> it = concepts.iterator();
		Concept whole = it.next();
		
		while (it.hasNext())
			whole = new UnionConcept(it.next(), whole);
		
		return whole;
	}
	
	public static Concept intersectionOfConcepts(Set<Concept> concepts) {
		if (concepts == null || concepts.isEmpty())
			return Constants.TOP_CONCEPT;
		
		Iterator<Concept> it = concepts.iterator();
		Concept whole = it.next();
		
		while (it.hasNext())
			whole = new IntersectionConcept(it.next(), whole);

		return whole;
	}
	
	public static Set<Concept> getIntersectionConcepts(Concept concept) {
		if (!(concept instanceof IntersectionConcept)) {
			Set<Concept> set = new HashSet<>(1);
			set.add(concept);
			return set;
		}

		Set<Concept> conceptList = new HashSet<>();
		Concept conceptA = concept.getConceptA();
		conceptList.addAll(getIntersectionConcepts(conceptA));
		
		Concept conceptB = ((IntersectionConcept)concept).getConceptB();
		conceptList.addAll(getIntersectionConcepts(conceptB));
		
		return conceptList;
	}
	
	public static Set<Concept> getUnionConcepts(Concept concept) {
		if (!(concept instanceof UnionConcept)) {
			Set<Concept> set = new HashSet<>(1);
			set.add(concept);
			return set;
		}
		
		Set<Concept> conceptList = new HashSet<>();
		Concept conceptA = concept.getConceptA();
		conceptList.addAll(getUnionConcepts(conceptA));
		Concept conceptB = ((UnionConcept)concept).getConceptB();
		conceptList.addAll(getUnionConcepts(conceptB));
		
		return conceptList;
	}
	
	public static void main(String[] main) {
		ConceptBuilder factory = new ConceptBuilder();
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		Concept concept;//= factory.c(A).union().c(B).intersection().c(A).union().c(B).build();
		Set<Concept> conSet = new HashSet<>(Arrays.asList(A, B, new IntersectionConcept(A, B)));
		for (Concept c : conSet) {
			System.out.println(">> " + c);
		}
		concept = unionOfConcepts(conSet);
		Set<Concept> unionConcepts = getUnionConcepts(concept);
		System.out.println(concept.toString());
		for (Concept c : unionConcepts)
			System.out.println("\t" + c);
		
		conSet = new HashSet<>(Arrays.asList(A, B, new IntersectionConcept(A, B)));
		concept = intersectionOfConcepts(conSet);
		Set<Concept> intersectionConcepts = getIntersectionConcepts(concept);
		System.out.println("\n" +concept.toString());
		for (Concept c : intersectionConcepts)
			System.out.println("\t" + c);
	}

}
