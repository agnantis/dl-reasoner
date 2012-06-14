package uom.dl.elements;

public interface Concept extends DLElement {
	public Concept getConceptA();
	public boolean isNNF();
	public Concept toNNF();
	public boolean isNegation();
	public boolean isAtomic();
}
