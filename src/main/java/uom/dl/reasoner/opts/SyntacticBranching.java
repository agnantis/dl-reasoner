package uom.dl.reasoner.opts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ClashException;
import uom.dl.reasoner.TList;
import uom.dl.utils.ConceptFactory;

public class SyntacticBranching {
private static final Logger log = LoggerFactory.getLogger(SemanticBranching.class);
	
	public static List<TList<Assertion>> apply(TList<Assertion> model) {
		Set<Concept> concepts = ConceptFactory.getUnionConcepts((Concept) model.getValue().getElement());
		Set<Assertion> assertions = ConceptFactory.createAssertions(concepts, model.getValue().getIndividualA());
		int noOfNewModels = assertions.size();
		List<TList<Assertion>> newModels = new ArrayList<>(noOfNewModels);
		int counter = 0;
		model.visited(true);
		Set<Integer> dSet = new HashSet<>(1);
		int bFactor = model.getRoot().getBranchDepthCounter();
		dSet.add(bFactor);			
		model.getRoot().incrementBranchDepthCounter();
		for (Assertion a : assertions) {
			TList<Assertion> newModel;
			//use the existing model, avoiding one unnecessary duplication 
			if (counter < noOfNewModels-1)
				newModel = TList.duplicate(model, false);
			else
				newModel = model;
			try {
				a.setBranchFactor(bFactor);
				a.setDependencySet(new HashSet<>(dSet));
				newModel.append(a);
				//newModel = newModel.getNext();
				newModels.add(newModel);
			} catch (ClashException e) {
				log.debug("Clash found. Model: " + newModel + " . Assertion: " + e.getAddedAssertion());
			}
			++counter;
		}
		return newModels;
	}
}
