package uom.dl.reasoner.opts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ClashException;
import uom.dl.reasoner.ConceptAssertion;
import uom.dl.reasoner.TList;
import uom.dl.reasoner.TableuaxConfiguration;
import uom.dl.reasoner.opts.Optimizations.Optimization;
import uom.dl.utils.ConceptFactory;

public class SemanticBranching {
	private static final Logger log = LoggerFactory.getLogger(SemanticBranching.class);
	
	public static List<TList<Assertion>> apply(TList<Assertion> model) {
		List<Assertion> unionAssertions = getUnvisitedUnionAssertions(model);
		Assertion assertionToSplit = null;
		if (TableuaxConfiguration.getConfiguration().getOptimizations().usesOptimization(Optimization.SEMANTIC_BRANCHING)) {
			assertionToSplit = new MOMSHeuristic().getBestSelection(unionAssertions);
		} else {
			//get a random
			Individual indA = unionAssertions.get(0).getIndividualA();
			Set<Concept> concepts = ConceptFactory.getUnionConcepts((Concept) unionAssertions.get(0).getElement());
			for (Concept c : concepts) {
				assertionToSplit = new ConceptAssertion(c, indA);
				break;
			}
		}
		//add the negation Assertion to the new model
		//complementModel.append(assertionToSplit.getNegation());
		//List with assertions to be added in the complement model
		List<Assertion> assertionsOfComplement = new ArrayList<>();
		assertionsOfComplement.add(assertionToSplit.getNegation());
		//mark visited
		for (Assertion ass : unionAssertions) {
			Set<Concept> unions = ConceptFactory.getUnionConcepts((Concept) ass.getElement());
			boolean found = unions.remove(assertionToSplit.getElement());
			if (found) {
				Concept newConcept = ConceptFactory.unionOfConcepts(unions);
				Assertion a = new ConceptAssertion(newConcept, ass.getIndividualA());
				assertionsOfComplement.add(a);
				model.setChildVisited(ass);
			}
		}
		//create the complement branch
		TList<Assertion> complementModel = TList.duplicate(model, false);
		List<TList<Assertion>> newModels = new ArrayList<>(2);
		try {
			//add selected Assertion to the existing model
			model.append(assertionToSplit);
			//move forward
			model = model.getNext();
			newModels.add(model);
		} catch (ClashException e) {
			log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAssertion());
		}
		try {
			//add to complement all new
			complementModel.append(assertionsOfComplement);
			//move forward
			complementModel = complementModel.getNext();
			newModels.add(complementModel);
		} catch (ClashException e) {
			log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAssertion());
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
