package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.reasoner.opts.Optimizations.Optimization;
import uom.dl.utils.NNFFactory;

public class TableauxAlgorithm {
	private static Logger log = LoggerFactory.getLogger(TableauxAlgorithm.class);
	private List<Model> invalidModels = new ArrayList<>(); 
	
	public Model findModel(Assertion assertion) {
		DLElement element = assertion.getElement();
		if (element instanceof Concept) {
			Concept c = (Concept) element;
			assertion = new ConceptAssertion(NNFFactory.getNNF(c), assertion.getIndividualA(), -1, new HashSet<Integer>());
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
		Assertion assertion = new ConceptAssertion(whole, new Individual("b"), -1, new HashSet<Integer>());
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
		while (true) {
			if (model.containsClash()) { //check for a clash
				Model m = new Model(model, false);
				return m;
			} else if (!model.canBeFurtherExpanded()) {//check for model
				return new Model(model, true); //model found
			}
			
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
					//if newModels,size = 1 -> expanding current
					if (newModels.size() == 1) {
						//deterministic expansion
						model = newModels.get(0);
						//rerun algorithm
						break;
					}
					
					//run tableaux to each new model
					Model expanded = null;
					if (TableauxConfiguration.getConfiguration().getOptimizations().usesOptimization(Optimization.DIRECTED_BACKTRACKING)) {
						expanded = expandWithBacktracking(model, newModels);
					} else {
						expanded = expandWithoutBacktracking(model, newModels);
					}					
					return expanded;
				} else {
					//value is atomic, so move to the next
					if (current.getNext() == null)
						return new Model(current, true);
					current = current.getNext();
				}			
			}
		}
	}
	
	private Model expandWithBacktracking(TList<Assertion> currentModel, List<TList<Assertion>> expansionModels) { 
		Set<Integer> clashSet = null;
		Model aModel = null;
		Set<Integer> totalClashSet = new HashSet<Integer>();
		int expansionsNo = expansionModels.size();
		int counter = 0;
		for (TList<Assertion> list : expansionModels){
			++counter;
			if (clashSet != null) {
				totalClashSet.addAll(clashSet);
				if (counter == expansionsNo) { //last branch
					list.getLeaf().getValue().setDependencySet(totalClashSet);
				}
				TList<Assertion> newList = aModel.getExtension();
				try {
					newList.append(list.getValue());
				} catch (ClashException e) {
					newList.getRoot().clashFound(e.getDependencyUnion());
				}
				list = newList;
			}
			aModel = runTableauxForConcept(list);
			if (aModel.isSatisfiable()) {
				//model found
				return aModel;
			} else {
				//clash found
				Model invalidModel = new Model(TList.duplicate(aModel.getExtension(), false, false), false);
				invalidModels.add(invalidModel);
				Set<Integer> dset = aModel.getExtension().getRoot().getClashDependencySet();
				//TListVisualizer.showGraph(aModel.getExtension().getRoot(), false);
				log.info("Clash found. Backtrack. DSet: " + dset);
				if (dset.isEmpty()) {
					//the clash depends on the root. //No solution available
					log.info("Dependency set is empty. The clash comes from root. No need for further search");
					return aModel;
				}
				clashSet = new HashSet<Integer>(dset);
				//bFactor > -1 -> branch position
				int bFactor = -1;
				while (bFactor < 0) {
					bFactor = aModel.getExtension().getLeaf().getValue().getBranchFactor(); 
					log.info("Leaf: " + aModel.getExtension().getLeaf().getValue());
					TList<Assertion> newTList = aModel.getExtension().removeLeaf();
					if (newTList == null) {
						//we reach the end. No further crop
						return aModel;
					}
					aModel.setExtension(newTList);
				}
				TList<Assertion> newTList = aModel.getExtension().removeLeaf();
				aModel.setExtension(newTList);
				if (clashSet.contains(bFactor)) {
					clashSet.remove(bFactor);
				} else {
					//do not check its siblings
					return aModel;
				}	
			}
		}
		//no model found
		return new Model(currentModel, false);		
	}
	
	private Model expandWithoutBacktracking(TList<Assertion> currentModel, List<TList<Assertion>> expansionModels) {
		//run tableaux to each new model
		for (TList<Assertion> list : expansionModels){
			Model aModel = runTableauxForConcept(list);
			if (aModel.isSatisfiable()) {
				//model found
				return aModel;
			} else {
				invalidModels.add(aModel);
			}
		}
		//no model found
		return new Model(currentModel, false);	
	}
	
	/**
	 * Returns all the models that provoked a clash
	 * @return all clashes
	 */
	public List<Model> getClashes() {
		return this.invalidModels;
	}
}
