package uom.dl.reasoner;

import java.util.List;

import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;

public interface Assertion extends DLElement {
	public DLElement getElement();
	public Individual getIndividualA();
	public void setIndividualA(Individual ind);
	public List<TList<Assertion>> executeRule(TList<Assertion> model);
	public Assertion getACopy();
	public Assertion getNegation();
}
