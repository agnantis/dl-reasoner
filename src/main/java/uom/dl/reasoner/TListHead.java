package uom.dl.reasoner;

public class TListHead<T extends Assertion> extends TList<T> {
	TriggerRules triggerRules;
	private boolean clashFound = false;

	public TListHead(T c) {
		super(c);
		this.triggerRules = new TriggerRules();
		this.triggerRules.setReceiver(this);
	}
	
	@Override
	public TListHead<T> getRoot() {
		return this;
	}
	
	public TriggerRules getTriggerRules() {
		return this.triggerRules;
	}

	public void clashFound() {
		this.clashFound = true;		
	}
	
	@Override
	public boolean containsClash() {
		return this.clashFound;
	}
}
