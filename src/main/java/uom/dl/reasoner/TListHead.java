package uom.dl.reasoner;

public class TListHead<T extends Assertion> extends TList<T> {
	TriggerRules triggerRules;

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
}
