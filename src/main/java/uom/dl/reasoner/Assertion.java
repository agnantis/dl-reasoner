package uom.dl.reasoner;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;

public interface Assertion extends DLElement {
	public Concept getElement();
	public Individual getIndividualA();
}
