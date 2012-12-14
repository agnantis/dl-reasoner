package uom.dl.utils;

import java.util.Comparator;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.DLElement;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.Assertion;

public class AssertionComparator implements Comparator<Assertion> {
	private static final Comparator<Assertion> comparator = new AssertionComparator();
	
	private AssertionComparator() {};
	
	public static Comparator<Assertion> getComparator() {
		return comparator;
	}		

	/**
	 * operation priorities for optimal size tableaux lists:
	 * [⊓,⊔] > [∃, ⩾] -> ⩽
	 * @param o1
	 * @param o2
	 * @return
	 */
	@Override
	public int compare(Assertion o1, Assertion o2) {
		DLElement e1 = o1.getElement();
		DLElement e2 = o2.getElement();
		//****FOR TESTING ONLY - REMOVE IT AFTERWARDS*****
		//if (e1 instanceof AtMostConcept)
		//	return -1;
		//if (e2 instanceof AtMostConcept)
		//	return 1;
		//****FOR TESTING ONLY - REMOVE IT AFTERWARDS*****
		if (e1 instanceof IntersectionConcept)
			return -1;
		if (e2 instanceof IntersectionConcept)
			return 1;
		if (e1 instanceof UnionConcept)
			return -1;
		if (e2 instanceof UnionConcept)
			return 1;
		if (e1 instanceof ExistsConcept)
			return -1;
		if (e2 instanceof ExistsConcept)
			return 1;
		if (e1 instanceof AtLeastConcept)
			return -1;
		if (e2 instanceof AtLeastConcept)
			return 1;
		return 0;
		
	}
}
