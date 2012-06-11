package uom.dl.elements;

public class IntersectionConcept implements Concept {
	private Concept concept1;
	private Concept concept2;

	public IntersectionConcept(Concept c, Concept d) {
		this.concept1 = c;
		this.concept2 = d;
	}
	
	@Override
	public String toString() {
		return "(" + this.concept1 + "⊓" + this.concept2 + ")";
	}

}
