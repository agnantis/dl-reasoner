package uom.dl.reasoner;

import java.util.HashSet;
import java.util.Set;


public class ClashException extends Exception {
	private static final long serialVersionUID = 5026636137165843983L;
	
	private final Assertion firstAssertion, secondAssertion;

	private Set<Integer> dependencyUnion;

	public ClashException(Assertion addedAssertion, Assertion existingAssertion) {
		super("Clash occur while inserting: " + addedAssertion);
		this.firstAssertion = addedAssertion;
		this.secondAssertion = existingAssertion;
		computeDependencyUnion();
	}

	public Assertion getAddedAssertion() {
		return firstAssertion;
	}
	
	public Assertion getExistingAssertion() {
		return secondAssertion;
	}
	
	public Set<Integer> getDependencyUnion() {
		return this.dependencyUnion;
	}
	
	private void computeDependencyUnion() {
		Set<Integer> unionSet = new HashSet<>(this.firstAssertion.getDependencySet());
		if (this.secondAssertion != null)
			unionSet.addAll(this.secondAssertion.getDependencySet());
		this.dependencyUnion = unionSet;
	}
}
