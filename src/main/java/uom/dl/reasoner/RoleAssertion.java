package uom.dl.reasoner;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class RoleAssertion implements BinaryAssertion {
	private final Concept role;
	private final Individual indA;
	private final Individual indB;

	public RoleAssertion(Role role, Individual indA, Individual indB) {
		this.role = role;
		this.indA = indA;
		this.indB = indB;
	}

	@Override
	public Concept getElement() {
		return role;
	}

	@Override
	public Individual getIndividualA() {
		return indA;
	}

	@Override
	public Individual getIndividualB() {
		return indB;
	}

	@Override
	public boolean isAtomic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComplement(DLElement other) {
		// TODO Auto-generated method stub
		return false;
	}

}