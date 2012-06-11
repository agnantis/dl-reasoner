package uom.dl.elements;

public class AtMostConcept implements Concept {
	private int number;
	private Concept concept;

	public AtMostConcept(int number, Concept concept) {
		this.number = number;
		this.concept = concept;
	}
	
	public AtMostConcept(int number) {
		this(number, Constants.TOP_CONCEPT);
	}
	
	@Override
	public String toString() {
		return "≤" + this.number + this.concept;
	}
}
