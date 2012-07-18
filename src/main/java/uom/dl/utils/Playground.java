package uom.dl.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.Individual;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ClashException;
import uom.dl.reasoner.ConceptAssertion;
import uom.dl.reasoner.TList;
import uom.dl.reasoner.TListHead;

public class Playground {
	
	public static void main(String[] args) throws ClashException {
		Concept A = new AtomicConcept("A");
		Concept B = new AtomicConcept("B");
		Concept C = new AtomicConcept("C");
		Concept D = new AtomicConcept("D");
		Concept E = new AtomicConcept("E");
		Concept F = new AtomicConcept("F");
		Concept G = new AtomicConcept("G");
		Individual indX = new Individual("x");
		Assertion aAs = new ConceptAssertion(A, indX, -1, new HashSet<Integer>());
		Assertion bAs = new ConceptAssertion(B, indX, -1, new HashSet<Integer>());
		Assertion cAs = new ConceptAssertion(C, indX, -1, new HashSet<Integer>());
		Assertion dAs = new ConceptAssertion(D, indX, -1, new HashSet<Integer>());
		//Assertion eAs = new ConceptAssertion(E, indX, -1, new HashSet<Integer>());
		Assertion fAs = new ConceptAssertion(F, indX, -1, new HashSet<Integer>());
		Assertion gAs = new ConceptAssertion(G, indX, -1, new HashSet<Integer>());
		TList<Assertion> head1 = new TListHead<Assertion>(aAs);
		head1.append(bAs);
		head1.append(cAs);
		head1.append(fAs);
		head1.getRoot().clashFound(new HashSet<Integer>());
		
		TList<Assertion> head2 = new TListHead<Assertion>(aAs);
		head2.append(bAs);
		head2.append(cAs);
		head2.append(gAs);
		head2.getRoot().clashFound(new HashSet<Integer>());
		
		TList<Assertion> head3 = new TListHead<Assertion>(aAs);
		head3.append(bAs);
		head3.append(dAs);
		head3.append(fAs);
		head3.getRoot().clashFound(new HashSet<Integer>());
		
		List<TList<Assertion>> allLists = new ArrayList<>(2);
		allLists.add(head1);
		allLists.add(head2);
		allLists.add(head3);
		
		TListVisualizer.showGraph(allLists, false);
		
	}
	

}
