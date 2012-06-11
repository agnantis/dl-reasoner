package uom.dl.elements;

public class NotConcept implements Concept {
	
	private Concept concept;

	public NotConcept(Concept concept){
		this.concept = concept;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "¬" + this.concept;
	}

}
