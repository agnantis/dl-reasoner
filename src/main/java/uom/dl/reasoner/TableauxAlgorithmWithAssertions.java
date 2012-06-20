package uom.dl.reasoner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;
import uom.dl.utils.TreeVisualizer;

public class TableauxAlgorithmWithAssertions {
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithmWithAssertions.class);
	
	public static boolean runTableauxForConcept(Assertion assertion) {
		List<TTree<Assertion>> frontier = new ArrayList<>();
		TTree<Assertion> tree = new TTree<Assertion>(assertion);
		frontier.add(tree);
		while (!frontier.isEmpty()) {
			TTree<Assertion> current = frontier.remove(0);
			Assertion value = current.getValue();
			value.executeRule(current);
			//check if a model exists
			if (!current.modelExists()) {
				printModel(tree, true);
				return false;
			}
			
			//add its children
			if (!current.getChildren().isEmpty())
				frontier.addAll(current.getChildren());
			
		}	
		printModel(tree, true);
		return true;
	}

	

	private static void printModel(TTree<Assertion> tree, boolean showModelImage) {
		System.out.println("Whole Model:");
		System.out.println(tree.print());
		System.out.println("--------------------");
		TreeVisualizer<Assertion> visual = new TreeVisualizer<Assertion>(tree);
		System.out.println(visual.toDotFormat());
		visual.saveGraph(Paths.get("/home/konstantine/Desktop/graph1.dot"));					
		visual.showGraph();
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
				new UnionConcept(new NotConcept(C), new NotConcept(B))
			));
		
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(D, A), 
				new UnionConcept(
						new IntersectionConcept(A, B),
						new UnionConcept(D, A)) 
			));
		
		Concept wholeConcept = ConceptFactory.intersectionOfConcepts(conSet);
		ConceptAssertion ca = new ConceptAssertion(wholeConcept, new Individual('b'));
		TableauxAlgorithmWithAssertions.runTableauxForConcept(ca);
	}
	
	
}
