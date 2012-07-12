package uom.dl.reasoner;


public class ClashException extends Exception {
	private static final long serialVersionUID = 5026636137165843983L;
	
	private final Assertion assertion;

	public ClashException(Assertion c) {
		super("Clash occur while insertinge: " + c);
		this.assertion = c;
	}

	public Assertion getAssertion() {
		return assertion;
	}
}
