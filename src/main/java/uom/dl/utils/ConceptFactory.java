package uom.dl.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.ConceptBuilder;
import uom.dl.elements.Constants;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;
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
	
	/*
	public static Set<Concept> getIntersectionConcepts(ConceptAssertion conceptAssertion) {
		Concept concept = conceptAssertion.getConcept();
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
	
	public static Set<DLElement> getIntersectionConcepts(DLElement concept) {
		
	}*/
	
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
	
	/*
	public static Set<ConceptAssertion> getUnionConcepts(ConceptAssertion conceptAssertion) {
		Concept concept = conceptAssertion.getConcept();
		if (!(concept instanceof UnionConcept)) {
			Set<ConceptAssertion> set = new HashSet<>(1);
			set.add(conceptAssertion);
			return set;
		}
		
		Set<ConceptAssertion> conceptList = new HashSet<>();
		Concept conceptA = concept.getConceptA();
		conceptList.addAll(getUnionConcepts(conceptA));
		Concept conceptB = ((UnionConcept)concept).getConceptB();
		conceptList.addAll(getUnionConcepts(conceptB));
		
		return conceptList;
	}*/
	
	/*
	public static boolean isComplement(Concept A, Concept B) {
		if (A.isAtomic() && B.isAtomic()) {
			if (A.isNegation())
				return A.getConceptA().equals(B);
			if (B.isNegation())
				return B.getConceptA().equals(A);
		}

		return false;
	}*/
	
	
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
		
		A = new AtomicConcept('A');
		Concept notA = new NotConcept(A);
		
		System.out.println("Is complement: " + A.isComplement(notA));
	}
	
	public static Set<ConceptAssertion> createAssertions(Set<Concept> concepts, Individual ind) {
		Set<ConceptAssertion> assertions = new HashSet<>(concepts.size());
		for (Concept c : concepts) {
			assertions.add(new ConceptAssertion(c, ind));
		}
		
		return assertions;
	}

}
