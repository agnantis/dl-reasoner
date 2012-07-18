package uom.dl.reasoner.opts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ClashException;
import uom.dl.reasoner.ConceptAssertion;
import uom.dl.reasoner.TList;
import uom.dl.reasoner.TableauxConfiguration;
import uom.dl.reasoner.opts.Optimizations.Optimization;
import uom.dl.utils.ConceptFactory;
import uom.dl.utils.NNFFactory;

public class SemanticBranching {
	private static final Logger log = LoggerFactory.getLogger(SemanticBranching.class);
	
	public static List<TList<Assertion>> apply(TList<Assertion> model) {
		List<Assertion> unionAssertions = getUnvisitedUnionAssertions(model);
		Assertion assertionToSplit = null;
		if (TableauxConfiguration.usesOptimization(Optimization.MOMS_HEURISTIC)) {
			assertionToSplit = new MOMSHeuristic().getBestSelection(unionAssertions);
		} else {
			//get a random
			Individual indA = unionAssertions.get(0).getIndividualA();
			Set<Concept> concepts = ConceptFactory.getUnionConcepts((Concept) unionAssertions.get(0).getElement());
			for (Concept c : concepts) {
				assertionToSplit = new ConceptAssertion(c, indA, -1, new HashSet<Integer>());
				break;
			}
		}
		//add the negation Assertion to the new model
		//complementModel.append(assertionToSplit.getNegation());
		//List with assertions to be added in the complement model
		//List<Assertion> assertionsOfComplement = new ArrayList<>();
		//assertionsOfComplement.add(assertionToSplit.getNegation(true));
		Concept negCon = (Concept) assertionToSplit.getNegation(true).getElement();
		//mark visited
		for (Assertion ass : unionAssertions) {
			Set<Concept> unions = ConceptFactory.getUnionConcepts((Concept) ass.getElement());
			boolean found = unions.remove(assertionToSplit.getElement());
			if (found) {
				Concept newConcept = ConceptFactory.unionOfConcepts(unions);
				//DO not put bcktrack info yet
				negCon = new IntersectionConcept(negCon, newConcept);
				//Assertion a = new ConceptAssertion(newConcept, ass.getIndividualA(), -1, new HashSet<Integer>());
				//assertionsOfComplement.add(a);
				model.setChildVisited(ass);
			}
		}
		//create the complement branch
		TList<Assertion> complementModel = null;
		if (!TableauxConfiguration.usesOptimization(Optimization.DIRECTED_BACKTRACKING))
			complementModel = TList.duplicate(model, false);
		
		List<TList<Assertion>> newModels = new ArrayList<>(2);
		try {
			//add selected Assertion to the existing model
			Set<Integer> dSet = new HashSet<>(1);
			int bFactor = model.getRoot().getBranchDepthCounter();
			dSet.add(bFactor);			
			assertionToSplit.setBranchFactor(bFactor);
			assertionToSplit.setDependencySet(dSet);
			model.getRoot().incrementBranchDepthCounter();
			model.append(assertionToSplit);
			//move forward
			//model = model.getNext();
			newModels.add(model);
		} catch (ClashException e) {
			log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
		}
		try {
			//add to complement all new
			Assertion aNeg = new ConceptAssertion(negCon, assertionToSplit.getIndividualA(), -1, new HashSet<Integer>());
			//complementModel.append(assertionsOfComplement);
			if (!TableauxConfiguration.usesOptimization(Optimization.DIRECTED_BACKTRACKING))
				complementModel.append(aNeg);
			else
				complementModel = new TList<Assertion>(aNeg);
			//move forward
			//complementModel = complementModel.getNext();
			newModels.add(complementModel);
		} catch (ClashException e) {
			log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
		}	
		return newModels;
	}
	
	public static List<Assertion> getUnvisitedUnionAssertions(final TList<Assertion> model) {
		List<Assertion> unionAssertions = new ArrayList<>();
		Individual ind = null;
		TList<Assertion> modelPnt = model;
		while (modelPnt != null) {
			Assertion ass = modelPnt.getValue();
			if (ind == null) { ind = ass.getIndividualA(); }
			if (!modelPnt.visited()) {
				DLElement el = ass.getElement();
				if (el instanceof UnionConcept) {
					if (ind.equals(ass.getIndividualA()))
						unionAssertions.add(ass);
				}				
			}
			modelPnt = modelPnt.getNext();
		}
		return unionAssertions;
	}
	
	
}
