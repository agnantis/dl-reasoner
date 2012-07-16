package uom.dl.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtMostConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TriggerRules {
	private static final Logger log = LoggerFactory.getLogger(TriggerRules.class);
	Set<ConceptAssertion> forAllRules = new HashSet<>();
	Set<ConceptAssertion> atMostRules = new HashSet<>();
	private TListHead<Assertion> list; 
	
	
	@SuppressWarnings("unchecked")
	public void setReceiver(TListHead<? extends Assertion> tList) {
		this.list = (TListHead<Assertion>) tList;
	}

	public boolean addRule(ForAllConcept c, Individual i, Set<Integer> dset) {
		return forAllRules.add(new ConceptAssertion(c, i, -1, dset));
	}
	
	public boolean addRule(AtMostConcept c, Individual i, Set<Integer> dset) {
		return atMostRules.add(new ConceptAssertion(c, i, -1, dset));
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
	
	public <T extends Assertion> void assertionAdded(RoleAssertion a) throws ClashException {
		Role role = a.getElement();
		Individual indA = a.getIndividualA();
		Individual indB = a.getIndividualB();
		for (ConceptAssertion ca : forAllRules) {
			Individual i = ca.getIndividualA();
			ForAllConcept c = (ForAllConcept) ca.getElement();
			Role r = c.getRole();
			if (role.equals(r) && indA.equals(i)) {
				//trigger rule
				int bFactor = -1;
				//new dSet = union of dependencies
				Set<Integer> dset = new HashSet<>(a.getDependencySet());
				dset.addAll(ca.getDependencySet());
				Assertion newAss = new ConceptAssertion(c.getConceptA(), indB, bFactor, dset);
				if (list != null) {
					log.debug("Rule triggered. Append to the model: " + newAss);
					list.append(newAss);
				}
			}			
		}
		
		for (ConceptAssertion ca : atMostRules) {
			Individual i = ca.getIndividualA();
			AtMostConcept c = (AtMostConcept) ca.getElement();
			Role r = c.getRole();
			if (role.equals(r) && indA.equals(i)) {
				//re-enter the rule
				if (list != null) {
					log.debug("Rule triggered. Append to the model: " + ca);
					list.append(ca, true);
				}
			}			
		}
			
	}

	public TriggerRules duplicate() {
		TriggerRules copy = new TriggerRules();
		for (ConceptAssertion ca : forAllRules) {
			copy.addRule(
					(ForAllConcept)ca.getElement(), 
					new Individual(ca.getIndividualA().getName()),
					ca.getDependencySet());
		}
		for (ConceptAssertion ca : atMostRules) {
			copy.addRule(
					(AtMostConcept)ca.getElement(), 
					new Individual(ca.getIndividualA().getName()),
					ca.getDependencySet());
		}
		return copy;
	}

}
