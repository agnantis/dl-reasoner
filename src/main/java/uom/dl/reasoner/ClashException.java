package uom.dl.reasoner;

import uom.dl.elements.Concept;

public class ClashException extends Exception {
	private static final long serialVersionUID = 5026636137165843983L;
	
	private final Concept concept;

	public ClashException(Concept c) {
		super("Concept Clash Detected: " + c);
		this.concept = c;
	}

	public Concept getConcept() {
		return concept;
	}
}
