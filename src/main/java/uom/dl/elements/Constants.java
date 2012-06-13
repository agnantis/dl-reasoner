package uom.dl.elements;

public class Constants {
	public static final char UNION_CHAR = '⊔';
	public static final char INTERSECTION_CHAR = '⊓';
	public static final char EXISTS_CHAR = '∃';
	public static final char FORALL_CHAR = '∀';
	public static final char ATMOST_CHAR = '⩽';
	public static final char ATLEAST_CHAR = '⩾';
	public static final char NOT_CHAR = '¬';
	public static final char TOPCONCEPT_CHAR = '⊤';
	public static final char BOTTOMCONCEPT_CHAR = '⊥';

	public static final Concept TOP_CONCEPT = new AtomicConcept(TOPCONCEPT_CHAR);
	public static final Concept BOTTOM_CONCEPT = new AtomicConcept(BOTTOMCONCEPT_CHAR);
}
