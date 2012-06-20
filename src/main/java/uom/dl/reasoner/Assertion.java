package uom.dl.reasoner;

import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;

public interface Assertion extends DLElement {
	public DLElement getElement();
	public Individual getIndividualA();
	public boolean executeRule(TTree<Assertion> model);
}
