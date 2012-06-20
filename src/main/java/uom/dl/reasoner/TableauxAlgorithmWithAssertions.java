package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;

public class TableauxAlgorithmWithAssertions {
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithmWithAssertions.class);
	
	public static Model runTableauxForConcept(Assertion assertion) {
		List<TTree<Assertion>> frontier = new ArrayList<>();
		TTree<Assertion> tree = new TTree<Assertion>(assertion);
		frontier.add(tree);
		while (!frontier.isEmpty()) {
			TTree<Assertion> current = frontier.remove(0);
			Assertion value = current.getValue();
			value.executeRule(current);
			//check if a model exists
			if (!current.modelExists()) {
				//printModel(tree, true);
				return new Model(tree, false);
			}
			
			//add its children
			if (!current.getChildren().isEmpty())
				frontier.addAll(current.getChildren());
			
		}	
		//printModel(tree, true);
		return new Model(tree, true);
	}

	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept C = new AtomicConcept("C");
		AtomicConcept D = new AtomicConcept("D");
		//HashSet<Concept> conSet = new HashSet<>(Arrays.asList(A, B, new UnionConcept(new IntersectionConcept(D, new NotConcept(C)), C), new NotConcept(D)));
		HashSet<Concept> conSet = new HashSet<>(Arrays.asList(
				(Concept)new IntersectionConcept(A, B), 
				new UnionConcept(new NotConcept(A), C), 
				new UnionConcept(new NotConcept(C), new NotConcept(B)),
				new ExistsConcept(new AtomicRole("R"), C)
			));
		/*
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(D, A), 
				new UnionConcept(
						new IntersectionConcept(A, B),
						new UnionConcept(D, A)) 
			));
		*/
		
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(D, A), 
				new UnionConcept(
						new IntersectionConcept(A, B),
						new ExistsConcept(new AtomicRole("R"), C)) 
			));
		Concept wholeConcept = ConceptFactory.intersectionOfConcepts(conSet);
		ConceptAssertion ca = new ConceptAssertion(wholeConcept, new Individual('b'));
		Model model = TableauxAlgorithmWithAssertions.runTableauxForConcept(ca);
		model.printModel(true);
		if (model.isSatisfiable())
			System.out.println(model.getInterpretation());
	}
	
	
}
