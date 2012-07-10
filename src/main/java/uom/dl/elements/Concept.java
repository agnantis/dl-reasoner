package uom.dl.elements;


public interface Concept extends DLElement {
	public Concept getConceptA();
	public void setConceptA(Concept concept);
	public boolean isNNF();
	public Concept toNNF();
	public boolean isNegation();
}
