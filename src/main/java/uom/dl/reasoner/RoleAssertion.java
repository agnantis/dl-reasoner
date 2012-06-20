package uom.dl.reasoner;

import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class RoleAssertion implements Assertion {
	private final Role role;
	private final Individual indA;
	private final Individual indB;

	public RoleAssertion(Role role, Individual indA, Individual indB) {
		this.role = role;
		this.indA = indA;
		this.indB = indB;
	}

	public Role getRole() {
		return role;
	}

	public Individual getIndA() {
		return indA;
	}

	public Individual getIndB() {
		return indB;
	}

}
