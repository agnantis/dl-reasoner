package uom.dl.reasoner;

public interface Rule {
	public boolean execute(TTree<Assertion> model, Assertion assertion);
}
