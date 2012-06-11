package uom.dl.elements;

public class AtLeastConcept implements Concept {
	private int number;
	private Concept concept;

	public AtLeastConcept(int number, Concept concept) {
		this.number = number;
		this.concept = concept;
	}
	
	public AtLeastConcept(int number) {
		this(number, Constants.TOP_CONCEPT);
	}
	
	@Override
	public String toString() {
		return "≥" + this.number + this.concept;
	}
}
