package uom.dl.utils;

import java.util.ArrayList;
import java.util.List;

import uom.dl.elements.Concept;
import uom.dl.elements.Constants;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;

public class ConceptFactory {
	
	public static Concept unionOfConcepts(List<Concept> concepts) {
		if (concepts == null || concepts.isEmpty())
			return Constants.TOP_CONCEPT;
		
		Concept whole = concepts.get(concepts.size()-1);
		
		for (int i = concepts.size() - 2; i >= 0 ; --i){
			whole = new UnionConcept(concepts.get(i), whole);
		}
		return whole;
	}
	
	public static Concept intersectionOfConcepts(List<Concept> concepts) {
		if (concepts == null || concepts.isEmpty())
			return Constants.TOP_CONCEPT;
		
		Concept whole = concepts.get(concepts.size()-1);
		
		for (int i = concepts.size() - 2; i >= 0 ; --i){
			whole = new IntersectionConcept(concepts.get(i), whole);
		}
		return whole;
	}
	
	public static List<Concept> getIntersectionConcepts(IntersectionConcept concept) {
		List<Concept> conceptList = new ArrayList<>();
		Concept conceptA = concept.getConceptA();
		if (conceptA instanceof IntersectionConcept)
			conceptList.addAll(getIntersectionConcepts((IntersectionConcept) conceptA));
		Concept conceptB = concept.getConceptB();
		if (conceptB instanceof IntersectionConcept)
			conceptList.addAll(getIntersectionConcepts((IntersectionConcept) conceptB));
		
		return conceptList;
	}
	
	public static List<Concept> getUnionConcepts(UnionConcept concept) {
		List<Concept> conceptList = new ArrayList<>();
		Concept conceptA = concept.getConceptA();
		if (conceptA instanceof UnionConcept)
			conceptList.addAll(getUnionConcepts((UnionConcept) conceptA));
		Concept conceptB = concept.getConceptB();
		if (conceptB instanceof IntersectionConcept)
			conceptList.addAll(getUnionConcepts((UnionConcept) conceptB));
		
		return conceptList;
	}

}
