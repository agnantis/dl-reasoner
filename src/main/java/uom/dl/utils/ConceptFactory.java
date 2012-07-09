package uom.dl.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.AtMostConcept;
import uom.dl.elements.AtomicConcept;
import uom.dl.elements.BinaryConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.Constants;
import uom.dl.elements.DLElement;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ConceptAssertion;

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
	
	/**
	 * Checks if a Concept c contains the AtomicConcept d in its declaration
	 * @param c a concept
	 * @param d an atomic concept
	 * @return true if the atomic concept d is contained in the concept c
	 */
	public static boolean contains(Concept c, AtomicConcept d) {
		return getAllAtomicConcepts(c).contains(d);
	}
	
	/**
	 * Replaces all the occurrences of the AtomicConcept d in the concept c with the Concept newD
	 * @param c a concept
	 * @param d the atomic concept to be replaced
	 * @param newD the new atomic concept 
	 * @return a new concept obtained by replaces all occurrences of d with newD 
	 */
	public static Concept replace(Concept c, AtomicConcept d, Concept newD) {
		if (c.equals(d))
			return newD;
		
		return null;
	}
	
	/**
	 * Traverses recursively the complex concepts and returns a set of all 
	 * atomic concepts that exist in Concept c.
	 * @param c a concept
	 * @return a Set<AtomicConcept> of atomic concepts
	 */
	public static Set<AtomicConcept> getAllAtomicConcepts(Concept c) {
		Set<AtomicConcept> setOfConcepts;
		if (c instanceof AtomicConcept) {
			setOfConcepts = new HashSet<>(1);
			setOfConcepts.add((AtomicConcept) c);
			return setOfConcepts;
		}
		//get first concept
		Concept c1 = c.getConceptA();
		setOfConcepts = getAllAtomicConcepts(c1);
		if (c instanceof BinaryConcept) {
			Concept c2 = ((BinaryConcept) c).getConceptB();
			setOfConcepts.addAll(getAllAtomicConcepts(c2));
		}
		return setOfConcepts;
	}
		
	public static void main(String[] main) {
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
		
		A = new AtomicConcept('A');
		Concept notA = new NotConcept(A);
		
		System.out.println("Is complement: " + A.isComplement(notA));
	}
	
	public static Set<Assertion> createAssertions(Set<Concept> concepts, Individual ind) {
		Set<Assertion> assertions = new HashSet<>(concepts.size());
		for (Concept c : concepts) {
			assertions.add(new ConceptAssertion(c, ind));
		}
		
		return assertions;
	}
	
	public final static Comparator<Assertion> ASSERTION_COMPARATOR = new Comparator<Assertion>() {

		/**
		 * operation priorities for optimal size tableaux lists:
		 * [⊓,⊔] > [∃, ⩾] -> ⩽
		 * @param o1
		 * @param o2
		 * @return
		 */
		@Override
		public int compare(Assertion o1, Assertion o2) {
			DLElement e1 = o1.getElement();
			DLElement e2 = o2.getElement();
			//****FOR TESTING ONLY - REMOVE IT AFTERWARDS*****
			//if (e1 instanceof AtMostConcept)
			//	return -1;
			//if (e2 instanceof AtMostConcept)
			//	return 1;
			//****FOR TESTING ONLY - REMOVE IT AFTERWARDS*****
			if (e1 instanceof BinaryConcept)
				return -1;
			if (e2 instanceof BinaryConcept)
				return 1;
			if (e1 instanceof ExistsConcept)
				return -1;
			if (e2 instanceof ExistsConcept)
				return 1;
			if (e1 instanceof AtLeastConcept)
				return -1;
			if (e2 instanceof AtLeastConcept)
				return 1;
			return 0;
			
		}
	};

}
