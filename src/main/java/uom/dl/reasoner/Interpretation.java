package uom.dl.reasoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Individual;
import uom.dl.reasoner.Interpretation.RoleCouple;

public class Interpretation {
	private Set<Individual> domain = new HashSet<>();
	private Map<AtomicConcept, Set<Individual>> conceptAssertions = new HashMap<>();
	private Map<AtomicRole, Set<RoleCouple>> roleAssertions = new HashMap<>();
	
	public Set<Individual> getDomain() {
		return domain;
	}

	public boolean addToDomain(Individual ind) {
		return this.domain.add(ind);
	}
	
	public Map<AtomicConcept, Set<Individual>> getConceptAssertions() {
		return conceptAssertions;
	}
	
	public boolean addConceptAssertion(AtomicConcept concept, Individual ind) {
		Set<Individual> aSet = conceptAssertions.get(concept);
		if (aSet == null)
			aSet = new HashSet<>();
		boolean returnValue = aSet.add(ind);
		conceptAssertions.put(concept, aSet);
		return returnValue;
	}
	
	public Map<AtomicRole, Set<RoleCouple>> getRoleAssertions() {
		return roleAssertions;
	}
	
	public boolean addRoleAssertion(AtomicRole role, RoleCouple roleCouple) {
		Set<RoleCouple> aSet = roleAssertions.get(role);
		if (aSet == null)
			aSet = new HashSet<>();
		boolean returnValue = aSet.add(roleCouple);
		roleAssertions.put(role, aSet);
		return returnValue;
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
