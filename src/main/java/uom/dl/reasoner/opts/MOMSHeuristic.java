package uom.dl.reasoner.opts;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.ConceptAssertion;
import uom.dl.utils.ConceptFactory;

public class MOMSHeuristic {
	public Assertion getBestSelection(List<Assertion> unionAssertions) {
		Map<Assertion, Integer> freqs = new HashMap<>();
		for (Assertion a : unionAssertions) {
			DLElement el = a.getElement();
			assertTrue(el instanceof Concept);
			Set<Concept> concepts = ConceptFactory.getUnionConcepts((Concept) el);
			for (Concept c : concepts) {
				ConceptAssertion ca = new ConceptAssertion(c, a.getIndividualA(), -1, new HashSet<Integer>());
				Integer count = freqs.get(ca);
				if (count == null)
					count = 1;
				else
					count += 1;
				freqs.put(ca, count);
			}
		}
		int max = -1;
		Assertion selected = null;
		for (Assertion a : freqs.keySet()) {
			Integer freq = freqs.get(a);
			if (freq > max) {
				max = freq;
				selected = a;
			}
		}
		return selected;
	}

}
