package uom.dl.reasoner;

import java.util.HashSet;
import java.util.Set;

public class TListHead<T extends Assertion> extends TList<T> {
	TriggerRules triggerRules;
	private boolean clashFound = false;
	private Set<Integer> clashDependencySet = new HashSet<>();
	private int branchDepthCounter = 1;

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

	public void clashFound(Set<Integer> clashDependencySet) {
		this.clashFound = true;		
		setClashDependencySet(clashDependencySet);
	}
	
	@Override
	public boolean containsClash() {
		return this.clashFound;
	}

	public int getBranchDepthCounter() {
		return branchDepthCounter;
	}

	public void setBranchDepthCounter(int currentBranch) {
		this.branchDepthCounter = currentBranch;
	}
	
	public void incrementBranchDepthCounter() {
		++this.branchDepthCounter;
	}
	
	public void decrementBranchDepthCounter() {
		--this.branchDepthCounter;
	}

	public Set<Integer> getClashDependencySet() {
		return clashDependencySet;
	}

	private void setClashDependencySet(Set<Integer> clashDependencySet) {
		this.clashDependencySet = clashDependencySet;
	}
}
