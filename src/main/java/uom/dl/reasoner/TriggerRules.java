package uom.dl.reasoner;

import java.util.HashSet;
import java.util.Set;

import uom.dl.elements.AtMostConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TriggerRules {
	Set<ConceptAssertion> forAllRules = new HashSet<>();
	Set<ConceptAssertion> atMostRules = new HashSet<>();
	private TList<Assertion> list;
	
	public TriggerRules(TList<Assertion> list) {
		this.list = list.getRoot(); 
	}
	
	public boolean addRule(ForAllConcept c, Individual i) {
		return forAllRules.add(new ConceptAssertion(c, i));
	}
	
	public boolean addRule(AtMostConcept c, Individual i) {
		return atMostRules.add(new ConceptAssertion(c, i));
	}
	
	public void substituteIndividual(Individual from, Individual to) {
		for (ConceptAssertion ca : forAllRules) {
			Individual i = ca.getIndividualA();
			if (from.equals(i))
				ca.setIndividualA(to);
		}
		for (ConceptAssertion ca : atMostRules) {
			Individual i = ca.getIndividualA();
			if (from.equals(i))
				ca.setIndividualA(to);
		}
	}
	
	public void assertionAdded(RoleAssertion a) {
		Role role = a.getElement();
		Individual indA = a.getIndividualA();
		Individual indB = a.getIndividualB();
		for (ConceptAssertion ca : forAllRules) {
			Individual i = ca.getIndividualA();
			ForAllConcept c = (ForAllConcept) ca.getElement();
			Role r = c.getRole();
			if (role.equals(r) && indA.equals(i)) {
				//trigger rule
				Assertion newAss = new ConceptAssertion(c.getConceptA(), indB);
				list.append(newAss);
			}			
		}
		
		for (ConceptAssertion ca : atMostRules) {
			Individual i = ca.getIndividualA();
			ForAllConcept c = (ForAllConcept) ca.getElement();
			Role r = c.getRole();
			if (role.equals(r) && indA.equals(i)) {
				//trigger rule
			}			
		}
			
	}

}
