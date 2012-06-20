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
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;
import uom.dl.utils.TreeVisualizer;

public class TableauxAlgorithm {
	/*
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithm.class);
	
	public static boolean runTableauxForConcept(Concept concept) {
		List<TTree<Concept>> frontier = new ArrayList<>();
		TTree<Concept> tree = new TTree<Concept>(concept);
		frontier.add(tree);
		while (!frontier.isEmpty()) {
			TTree<Concept> current = frontier.remove(0);
			//An atomic concept cannot be expanded. Add
			if (!current.getValue().isAtomic()) {
				//execute intersection rule
				Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(current.getValue());
				if (concepts.size() > 1) {
					current.append(new ArrayList<>(concepts), TTree.ADD_IN_SEQUENCE);
				}
				//execute union rule
				concepts = ConceptFactory.getUnionConcepts(current.getValue());
				if (concepts.size() > 1) {
					current.append(new ArrayList<>(concepts), TTree.ADD_IN_PARALLEL);
				}
				//check if a model exists
				if (!current.modelExists()) {
					log.info("No model exists. Concept is unsatisfiable: " + concept);
					System.out.println("Whole Model:");
					System.out.println(tree.print());
					System.out.println("--------------------");
					TreeVisualizer<Concept> visual = new TreeVisualizer<Concept>(tree);
					System.out.println(visual.toDotFormat());
					visual.saveGraph(Paths.get("/home/konstantine/Desktop/graph1.dot"));					
					visual.showGraph();
					System.out.println("--------------------");
					System.out.println("Current Model:");
					System.out.println(current.print());
					return false;
				}
			}
			
			//add its children
			if (!current.getChildren().isEmpty())
				frontier.addAll(current.getChildren());
			
		}		
		System.out.println("The Model:");
		System.out.println(tree.print());
		System.out.println("--------------------");
		TreeVisualizer<Assertion> visual = new TreeVisualizer<Assertion>(tree);
		System.out.println(visual.toDotFormat());
		visual.saveGraph(Paths.get("/home/konstantine/Desktop/graph1.dot"));
		visual.showGraph();
		System.out.println("--------------------");
		return true;
	}
		
	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept C = new AtomicConcept("C");
		//AtomicConcept D = new AtomicConcept("D");
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
		
		TableauxAlgorithm.runTableauxForConcept(ConceptFactory.intersectionOfConcepts(conSet));
	}*/
	
	
}
