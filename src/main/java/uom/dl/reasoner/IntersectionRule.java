package uom.dl.reasoner;


public class IntersectionRule implements Rule {

	@Override
	public boolean execute(TTree<Assertion> model, Assertion assertion) {
		return true;
	}

}
