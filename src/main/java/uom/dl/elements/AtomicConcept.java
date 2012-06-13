package uom.dl.elements;

public class AtomicConcept implements Concept {
	private String name;

	public AtomicConcept(char name) {
		this(name + "");
	}
	
	public AtomicConcept(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}

}
