package uom.dl.reasoner;

import java.util.Arrays;
import java.util.List;

import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class RoleAssertion implements BinaryAssertion {
	private final Role role;
	private Individual indA;
	private Individual indB;

	public RoleAssertion(Role role, Individual indA, Individual indB) {
		this.role = role;
		this.indA = indA;
		this.indB = indB;
	}

	@Override
	public DLElement getElement() {
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
		return true;
	}

	@Override
	public boolean isComplement(DLElement other) {
		if (!(other instanceof RoleAssertion))
			return false;
		
		RoleAssertion ass = (RoleAssertion) other;
		return ass.getIndividualA().equals(getIndividualA())
				&& ass.getIndividualB().equals(getIndividualB())
				&& ass.getElement().isComplement(getElement());
	}

	@Override
	public List<TList<Assertion>> executeRule(TList<Assertion> model) {
		return Arrays.asList(model);
	}
	
	@Override
	public String toString() {
		return this.getElement() + "(" + this.getIndividualA() + "," + this.getIndividualB() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RoleAssertion))
			return false;
		RoleAssertion other = (RoleAssertion) obj;
		return getIndividualA().equals(other.getIndividualA()) 
				&& getIndividualB().equals(other.getIndividualB())
				&& getElement().equals(other);
		
	}
	
	@Override
	public void setIndividualA(Individual ind) {
		this.indA = ind;
	}
	
	@Override
	public void setIndividualB(Individual ind) {
		this.indB = ind;
	}
	
	@Override
	public Assertion getACopy() {
		Individual ind1 = new Individual(this.indA.getName());
		Individual ind2 = new Individual(this.indB.getName());
		RoleAssertion ass = new RoleAssertion(this.role, ind1, ind2);
		return ass;
	}

}
