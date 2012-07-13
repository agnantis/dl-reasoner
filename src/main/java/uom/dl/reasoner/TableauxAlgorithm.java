package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.utils.NNFFactory;

public class TableauxAlgorithm {
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithm.class);
	private List<Model> invalidModels = new ArrayList<>(); 
	
	public Model findModel(Assertion assertion) {
		DLElement element = assertion.getElement();
		if (element instanceof Concept) {
			Concept c = (Concept) element;
			assertion = new ConceptAssertion(NNFFactory.getNNF(c), assertion.getIndividualA());
		}
		TListHead<Assertion> list = new TListHead<Assertion>(assertion);
		return runTableauxForConcept(list);
	}
	
	//subsumee <= subsumer --> subsumee and not subsumer = 0
	/**
	 * 
	 * @param subsumer the candidate subsumer
	 * @param subsumee the candidate subsumee
	 * @return true if the subsumer is a superset of subsumee
	 */
	public boolean subsumes(Concept subsumer, Concept subsumee) {
		log.info("Check if: " + subsumee + " ⊑ " + subsumer);
		Concept negC2 = new NotConcept(subsumer);
		log.debug("Convert subsumer to negative: " + negC2);
		negC2 = NNFFactory.getNNF(negC2);
		log.debug("Convert subsumer to NNF: " + negC2);
		Concept nnfC1 = NNFFactory.getNNF(subsumee);
		log.debug("Convert subsumee to NNF: " + nnfC1);
		Concept whole = new IntersectionConcept(nnfC1, negC2);
		Assertion assertion = new ConceptAssertion(whole, new Individual("b"));
		log.debug("Find Model for: " + assertion);
		Model model = findModel(assertion);
		//System.out.println(model.getInterpretation());
		//model.printModel(true);
		boolean modelFound = model.isSatisfiable();
		if (modelFound) {
			log.debug("Model found for: " + assertion);
			log.info("No subsumption: " + subsumee + " ⋢ " + subsumer);
		} else {			
			log.debug("No model found for: " + assertion);
			log.info("No subsumption: " + subsumee + " ⊑ " + subsumer);
		}
				
		return !modelFound;
		
	}
		
	private Model runTableauxForConcept(TList<Assertion> model) {
		TList<Assertion> current = model;
		List<TList<Assertion>> newModels = new ArrayList<>();
		
		while (true) {
			Assertion value = current.getValue();
			if (!current.visited() && !value.isAtomic()) {
				newModels = value.executeRule(current);
				//if the model is empty, it means that we reach the end
				//no clash found, so the model is satisfiable!
				if (newModels.isEmpty()) {
					return new Model(current, true);
				}
				//check if a model exists
				for (Iterator<TList<Assertion>> it = newModels.iterator(); it.hasNext();) {
					TList<Assertion> newModel = it.next();
					//check for a clash
					if (newModel.containsClash()) {
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
	
	/**
	 * Returns all the models that provoked a clash
	 * @return all clashes
	 */
	public List<Model> getClashes() {
		return this.invalidModels;
	}
}
