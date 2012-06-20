package uom.dl.reasoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class Interpretation {
	private Set<Individual> domain = new HashSet<>();
	private Map<Concept, Set<Individual>> conceptAssertions = new HashMap<>();
	private Map<Role, Set<RoleCouple>> roleAssertions = new HashMap<>();
	
	public Set<Individual> getDomain() {
		return domain;
	}

	public boolean addToDomain(Individual ind) {
		return this.domain.add(ind);
	}
	
	public Map<Concept, Set<Individual>> getConceptAssertions() {
		return conceptAssertions;
	}
	
	private boolean addConceptAssertion(Concept concept, Individual ind) {
		Set<Individual> aSet = conceptAssertions.get(concept);
		if (aSet == null)
			aSet = new HashSet<>();
		boolean returnValue = aSet.add(ind);
		conceptAssertions.put(concept, aSet);
		return returnValue;
	}
	
	public boolean addAssertion(Assertion assertion) {
		addToDomain(assertion.getIndividualA());
		if (assertion instanceof ConceptAssertion) {
			ConceptAssertion ca = (ConceptAssertion) assertion;
			if (ca.isAtomic()) 
				return addConceptAssertion((Concept)ca.getElement(), ca.getIndividualA());
			
		}
		if (assertion instanceof RoleAssertion) {
			RoleAssertion ra = (RoleAssertion) assertion;
			return addRoleAssertion((Role) ra.getElement(), ra.getIndividualA(), ra.getIndividualB());
		}
		return false;
	}
	
	public Map<Role, Set<RoleCouple>> getRoleAssertions() {
		return roleAssertions;
	}
	
	private boolean addRoleAssertion(Role role, Individual indA, Individual indB) {
		Set<RoleCouple> aSet = roleAssertions.get(role);
		if (aSet == null)
			aSet = new HashSet<>();
		boolean returnValue = aSet.add(new RoleCouple(indA, indB));
		roleAssertions.put(role, aSet);
		return returnValue;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Model Interpretation:\n");
		if (!getDomain().isEmpty()) {
			sb.append("  Δ = {");
			for (Individual i : getDomain()) {
				sb.append(i + ","); 
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("}\n");
		}
		if (!roleAssertions.keySet().isEmpty()) {
			for (Role c : roleAssertions.keySet()) {
				Set<RoleCouple> couple = roleAssertions.get(c);
				sb.append("  " + c + " = {");
				for (RoleCouple ind : couple) {
					sb.append(ind + ", ");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("}\n");
			}
			sb.append("}\n");
		}
		if (!conceptAssertions.keySet().isEmpty()) {
			for (Concept c : conceptAssertions.keySet()) {
				Set<Individual> inds = conceptAssertions.get(c);
				sb.append("  " + c + " = {");
				for (Individual ind : inds) {
					sb.append(ind + ",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append("}\n");
			}			
		}
		return sb.toString(); 
	}
	
	
	public static class RoleCouple {
		private final Individual firstInd;
		private final Individual secondInd;
		
		public RoleCouple(Individual firstInd, Individual secondInd) {
			this.firstInd = firstInd;
			this.secondInd = secondInd;
		}

		public Individual getFirstInd() {
			return firstInd;
		}

		public Individual getSecondInd() {
			return secondInd;
		}
		
		public String toString(){
			return "(" + getFirstInd() + ", " + getSecondInd() + ")";
		}
		
		@Override
		public int hashCode() {
			return 31*getFirstInd().hashCode() + getSecondInd().hashCode();  
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof RoleCouple))
				return false;
			RoleCouple that = (RoleCouple) obj;
			
			return this.getFirstInd().equals(that.getFirstInd())
					&& this.getSecondInd().equals(that.getSecondInd());
		}
	}



	

}
