package uom.dl.reasoner.opts;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ClashException;
import uom.dl.reasoner.ConceptAssertion;
import uom.dl.reasoner.TList;
import uom.dl.utils.AssertionComparator;
import uom.dl.utils.ConceptFactory;

public class LocalSimplification {
	private static Map<Assertion, Assertion> getBestSelection(List<Assertion> unionAssertions, List<Assertion> atomicAssertions) throws ClashException {
		Map<Assertion, Assertion> simplified = new HashMap<>();
		
		Set<Concept> atAssSet = new HashSet<>();
		for (Assertion atAss : atomicAssertions) {
			if (atAss.getElement() instanceof Concept) {
				Concept cNeg = ConceptFactory.getNegation((Concept) atAss.getElement());
				atAssSet.add(cNeg);
			}
		}
		for (Assertion a : unionAssertions) {
			DLElement el = a.getElement();
			assertTrue(el instanceof Concept);
			Set<Concept> concepts = ConceptFactory.getUnionConcepts((Concept) el);
			boolean changed = concepts.removeAll(atAssSet);
			if (!changed) {
				//no simplification occurred
				continue;
			}
			//is there a clash?
			if (concepts.size() == 0) {
				//Select one arbitrary removed concept
				Concept arbitraryConcept = ((Concept) a.getElement()).getConceptA();
				Assertion tmpAss = new ConceptAssertion(arbitraryConcept, a.getIndividualA());
				throw new ClashException(tmpAss);
			}
			Assertion tmpAss = new ConceptAssertion(ConceptFactory.unionOfConcepts(concepts), a.getIndividualA());
			simplified.put(a, tmpAss);
		}
				
		return simplified;
	}
	
	public static TList<Assertion> apply(TList<Assertion> model) throws ClashException {
		List<Assertion> unionAssertions = SemanticBranching.getUnvisitedUnionAssertions(model);
		List<Assertion> atomicAssertions = getAtomicAssertions(model);
		Map<Assertion, Assertion> simplifiedAssertions = getBestSelection(unionAssertions, atomicAssertions);
		List<Assertion> children = new ArrayList<>();
		//mark visited
		for (Assertion ass : unionAssertions) {
			//check if assertion simplified
			if (simplifiedAssertions.containsKey(ass)) {
				//mark visited
				model.setChildVisited(ass);
				//expand assertion with its simplification
				children.add(simplifiedAssertions.get(ass));
			}
		}
		Collections.sort(children, AssertionComparator.getComparator());
		model.append(children);
		return model;
	}
	
	private static List<Assertion> getAtomicAssertions(final TList<Assertion> model) {
		List<Assertion> assertionList = new ArrayList<>();
		TList<Assertion> ptr = model.getRoot();
		while (ptr != null) {
			Assertion value = ptr.getValue();
			if (value.isAtomic())
				assertionList.add(value);
			ptr = ptr.getNext();
		}
		return assertionList;
	}
	
	/*
	public static class LSStruct {
		public final Assertion selectedAssertion;
	    public final Assertion expandedAssertion;

	    public LSStruct(Assertion selectedAssertion, Assertion expandedAssertion) {
	        this.selectedAssertion = selectedAssertion;
	        this.expandedAssertion = expandedAssertion;
	    }
	}*/

}
