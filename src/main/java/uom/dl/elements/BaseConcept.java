package uom.dl.elements;

public class BaseConcept implements Concept {
	private String name;

	public BaseConcept(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}

}
