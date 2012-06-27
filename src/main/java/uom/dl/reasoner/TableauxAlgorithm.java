package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtMostConcept;
import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.ConceptFactory;

public class TableauxAlgorithm {
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithm.class);
	private static List<Model> invalidModels = new ArrayList<>(); 
	
	public static Model findModel(Assertion assertion) {
		TList<Assertion> list = new TList<Assertion>(assertion);
		return runTableauxForConcept(list);
	}
		
	private static Model runTableauxForConcept(TList<Assertion> model) {
		TList<Assertion> current = model;
		List<TList<Assertion>> newModels = new ArrayList<>();
		
		while (true) {
			Assertion value = current.getValue();
			if (!value.isAtomic()) {
				newModels = value.executeRule(current);
				//check if a model exists
				for (Iterator<TList<Assertion>> it = newModels.iterator(); it.hasNext();) {
					TList<Assertion> newModel = it.next();
					//check for a clash
					if (!newModel.modelExists()) {
						invalidModels.add(new Model(newModel, false));
						//discard model
						it.remove();
					} else { //check for model
						if (!newModel.canBeFurtherExpanded()) {
							//model found
							return new Model(newModel, true);
						}
					}
				}
				//run tableaux to each new model
				if (!newModels.isEmpty()) {
					for (TList<Assertion> list : newModels){
						Model aModel = runTableauxForConcept(list);
						if (aModel.isSatisfiable()) {
							//model found
							return aModel;
						} 
					}
				}
				//no model found
				return new Model(model, false);
			} else {
				//value is atomic, so move to the next
				if (current.getNext() == null)
					return new Model(current, true);
				current = current.getNext();
			}			
		}
	}

	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept C = new AtomicConcept("C");
		AtomicConcept D = new AtomicConcept("D");
		AtomicRole R = new AtomicRole("R");
		//HashSet<Concept> conSet = new HashSet<>(Arrays.asList(A, B, new UnionConcept(new IntersectionConcept(D, new NotConcept(C)), C), new NotConcept(D)));
		HashSet<Concept> conSet = new HashSet<>(Arrays.asList(
				(Concept)new IntersectionConcept(A, B), 
				new UnionConcept(new NotConcept(A), C), 
				new UnionConcept(new NotConcept(C), new NotConcept(B)),
				new ExistsConcept(new AtomicRole("V"), new ExistsConcept(new AtomicRole("R"), C))
			));
		
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(D, A), 
				new UnionConcept(
						new IntersectionConcept(A, B),
						new UnionConcept(D, A)) 
			));
		
		/*
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(D, A), 
				new UnionConcept(
						new IntersectionConcept(A, B),
						new ExistsConcept(new AtomicRole("R"), new ExistsConcept(new AtomicRole("R"), C)))
			));
		*/
		conSet = new HashSet<>(Arrays.asList(
				new ExistsConcept(R, A),
				new ExistsConcept(R, B),
				new ForAllConcept(R, 
						new UnionConcept(new NotConcept(A), new NotConcept(B)))
			));
		
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new ExistsConcept(R, A),
				new ExistsConcept(R, B),
				//new ExistsConcept(R, C),
				new ForAllConcept(R, new UnionConcept(new NotConcept(A), new NotConcept(B))),
				new AtMostConcept(1, R)
				//new AtLeastConcept(3, R)
			));
		/*
		Role p1 = new AtomicRole("P1");
		Role p2 = new AtomicRole("P2");
		Role p3 = new AtomicRole("P3");
		Concept c11 = new AtomicConcept("C11");
		Concept c12 = new AtomicConcept("C12");
		Concept c21 = new AtomicConcept("C21");
		Concept c22 = new AtomicConcept("C22");
		Concept c31 = new AtomicConcept("C31");
		Concept c32 = new AtomicConcept("C32");
		
		Set<Concept> set1 = new HashSet<>();
		set1.add(new ExistsConcept(p3, c31));
		set1.add(new ExistsConcept(p3, c32));
		Concept conA = new IntersectionConcept(
				new ExistsConcept(p3, c31), 
				new ExistsConcept(p3, c32));
		Concept con1 = new ForAllConcept(p2, conA);
		Concept con2 = new ExistsConcept(p2, 
				new  ForAllConcept(p3, c22));
		Concept con3 = new ExistsConcept(p2, 
				new  ForAllConcept(p3, c21));
		
		Concept con321 = ConceptFactory.intersectionOfConcepts(
				new HashSet<>(Arrays.asList(con3, con2, con1)));
		
		
		conSet = new HashSet<>(Arrays.asList(
				(Concept) new ExistsConcept(p1, new ForAllConcept(p2, new ForAllConcept(p3, c11))),
				new ExistsConcept(p1, new ForAllConcept(p2, new ForAllConcept(p3, c12))),
				new ForAllConcept(p1, con321),
				new AtMostConcept(1, p1) 
			));
		/*
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new NotConcept(A),
				A
			));
		conSet = new HashSet<>(Arrays.asList(
				(Concept)new UnionConcept(A, B),
				new ExistsConcept(R, A),
				new NotConcept(A)
			));*/
		Concept wholeConcept = ConceptFactory.intersectionOfConcepts(conSet);
		ConceptAssertion ca = new ConceptAssertion(wholeConcept, new Individual('b'));
		Model model = TableauxAlgorithm.findModel(ca);
		if (model.isSatisfiable()) {
			System.out.println(model.getInterpretation());
			model.printModel(true);
			System.out.println("No of models: " + TList.NO_OF_DUPLICATES+1);
		} else {
			System.out.println("No Valid Interpretation");
			Model.printModel(invalidModels, true);
		}
	}	
	
}
