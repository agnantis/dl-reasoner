package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.AtMostConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.Individual;
import uom.dl.elements.Individual.IndividualPair;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;
import uom.dl.reasoner.opts.LocalSimplification;
import uom.dl.reasoner.opts.Optimizations.Optimization;
import uom.dl.reasoner.opts.SemanticBranching;
import uom.dl.reasoner.opts.SyntacticBranching;
import uom.dl.utils.AssertionComparator;
import uom.dl.utils.ConceptFactory;
import uom.dl.utils.NNFFactory;

public class ConceptAssertion implements Assertion {
	private static final Logger log = LoggerFactory.getLogger(ConceptAssertion.class);
	private final Concept concept;
	private Individual ind;
	private int branchFactor = -1;
	private Set<Integer> dependencySet = new HashSet<>();
	
	/*
	public ConceptAssertion(Concept concept, Individual ind) {
		this.concept = concept;
		this.ind = ind;
	}*/
	
	public ConceptAssertion(Concept concept, Individual ind, int bFactor, Set<Integer> dependencySet) {
		this.concept = concept;
		this.ind = ind;
		this.branchFactor = bFactor;
		this.dependencySet = dependencySet;
	}

	@Override
	public Concept getElement() {
		return concept;
	}

	@Override
	public Individual getIndividualA() {
		return ind;
	}

	@Override
	public boolean isAtomic() {
		return concept.isAtomic();
	}

	@Override
	public boolean isComplement(DLElement obj) {
		if (!(obj instanceof ConceptAssertion))
			return false;
		Assertion other = (Assertion) obj;
		return other.getIndividualA().equals(getIndividualA())
				&& other.getElement().isComplement(getElement());
	}
	
	@Override
	public boolean canHaveComplement() {
		return true;
	}
	
	@Override
	public int getBranchFactor() {
		return this.branchFactor;
	}
	
	@Override
	public void setBranchFactor(int bFactor) {
		this.branchFactor = bFactor;
	}
	
	@Override
	public Set<Integer> getDependencySet() {
		return this.dependencySet;
	}
	
	@Override
	public void setDependencySet(Set<Integer> dset) {
		this.dependencySet = dset;		
	}
	
	@Override
	public String toString() {
		if (this.getElement().isAtomic()) {
			return this.getElement() + "(" + this.getIndividualA() + ") " + getBranchFactor() + ", " + getDependencySet();	
		}
		return "(" + this.getElement() + ")" + "(" + this.getIndividualA() + ") " + getBranchFactor() + ", " + getDependencySet();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConceptAssertion))
			return false;
		ConceptAssertion other = (ConceptAssertion) obj;
		return getIndividualA().equals(other.getIndividualA()) && getElement().equals(other.getElement());
		
	}
	
	@Override
	public int hashCode() {
		return getIndividualA().hashCode() + getElement().hashCode();
	}

	@Override
	public List<TList<Assertion>> executeRule(TList<Assertion> model) {
		if (concept instanceof IntersectionConcept) {
			Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(concept);
			Set<Assertion> assertions = new HashSet<>(concepts.size());
			for (Concept c : concepts) {
				Assertion a;
				//remove if.the
				//DEBUG_ONLY
				/*
				if (c instanceof ForAllConcept) {
					Set<Integer> test1 = new HashSet<Integer>();
					test1.add(3);
					a = new ConceptAssertion(c, this.getIndividualA(), -1, test1);
				} else if (c instanceof ExistsConcept) {
					Set<Integer> test1 = new HashSet<Integer>();
					test1.add(4);
					a = new ConceptAssertion(c, this.getIndividualA(), -1, test1);
				} else*/
				a = new ConceptAssertion(c, this.getIndividualA(), -1, this.getDependencySet());
				assertions.add(a);
			}
			List<Assertion> assList = new ArrayList<>(assertions);
			Collections.sort(assList, AssertionComparator.getComparator());
			try {
				model.append(assList);
				model.visited(true);
				//model = model.getNext();
			} catch (ClashException e) {
				log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
				if (e.getAddedAssertion().getDependencySet().isEmpty()) {
					log.debug("Concept is UNSATISFIABE!"); 
				}
			} 
			List<TList<Assertion>> list = new ArrayList<>();
			list.add(model);
			return list;
		}
		if (concept instanceof UnionConcept) {
			List<TList<Assertion>> newModels;
			if (TableauxConfiguration.getConfiguration().getOptimizations().usesOptimization(Optimization.LOCAL_SIMPLIFICATION)) {
				try {
					model = LocalSimplification.apply(model);
					if (model.visited()) {
						//current UnionConcept has simplified, so do not continue here
						List<TList<Assertion>> list = new ArrayList<>();
						list.add(model);
						return list;
					}
				} catch (ClashException e) {
					model.getRoot().clashFound(e.getDependencyUnion());
					log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
				}
				List<TList<Assertion>> list = new ArrayList<>();
				list.add(model);
				return list; 
			}
			
			if (TableauxConfiguration.getConfiguration().getOptimizations().usesOptimization(Optimization.SEMANTIC_BRANCHING)) {
				newModels = SemanticBranching.apply(model);
			} else {
				//newModels = syntacticUnionRule(model);
				newModels = SyntacticBranching.apply(model);
			}
			return newModels;				
		}
		if (concept instanceof ForAllConcept) {
			ForAllConcept ec = (ForAllConcept) concept;
			Concept c = ec.getConceptA();
			Role role = ec.getRole();
			//Set<Individual> casesBeAdded = model.getUnspecifiedFiller(role, c, getIndividualA());
			Map<Individual, Set<Integer>> casesBeAdded = model.getUnspecifiedFillerWithDependencies(role, c, getIndividualA());
			//List<Assertion> toBeAdded = new ArrayList<>(casesBeAdded.size());
			//add C(i)
			Set<Integer> currentDSet = this.getDependencySet();
			//int bFactor = model.getBranchingFactor();//use always -1???
			try {
				for (Individual i : casesBeAdded.keySet()) {
					//toBeAdded.add(new ConceptAssertion(c, i));
					Set<Integer> newDSet = new HashSet<>(casesBeAdded.get(i));
					newDSet.addAll(currentDSet);
					Assertion ca = new ConceptAssertion(c, i, -1, newDSet);
					model.append(ca);//dependency the concept itself, and the R(x,y) statement
				}
				model.visited(true);
				//model = model.getNext();
				//add to trigger list
				model.getRoot().getTriggerRules().addRule(ec, getIndividualA(), getDependencySet());
			} catch (ClashException e) {
				log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
			}
			List<TList<Assertion>> list = new ArrayList<>();
			list.add(model);
			return list;
		}
		if (concept instanceof ExistsConcept) {
			ExistsConcept ec = (ExistsConcept) concept;
			Concept c = ec.getConceptA();
			Role role = ec.getRole();
			boolean containsInd = model.containsFiller(role, c, getIndividualA());
			//model.visited(true);
			if (!containsInd) {
				Set<Integer> currentDSet = this.getDependencySet();
				//int bFactor = model.getBranchingFactor();
				//create a random individual
				Individual newInd = new Individual();
				//add C(x), R(b,x)
				Assertion ca = new ConceptAssertion(c, newInd, -1, currentDSet);
				Assertion ra = new RoleAssertion(role, getIndividualA(), newInd, -1, currentDSet);
				List<Assertion> alist = new ArrayList<>(2);
				alist.add(ca);
				alist.add(ra);
				try { 
					model.append(alist);
				} catch (ClashException e) {
					log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
					List<TList<Assertion>> list = new ArrayList<>();
					list.add(model);
					return list;
				}
			}
			model.visited(true);
			//model = model.getNext();
			List<TList<Assertion>> list = new ArrayList<>();
			list.add(model);
			return list;
		}
		if (concept instanceof AtMostConcept) {
			AtMostConcept amc = (AtMostConcept) concept;
			Role role = amc.getRole();
			int card = amc.getCardinality();
			Set<Individual> allFillers = model.getFillers(role, ind);
			List<TList<Assertion>> newModels = new ArrayList<>(allFillers.size());
			if (allFillers.size() > card) {
				//find all possible couples
				List<IndividualPair> subPairs = Individual.getPairs(new ArrayList<>(allFillers));
				//create models for each substitution
				int counter = 0;
				for (IndividualPair pair : subPairs) {
					TList<Assertion> newModel;
					//use the existing model, avoiding one unnecessary duplication 
					if (counter < subPairs.size()-1)
						newModel = TList.duplicate(model, true);
					else
						newModel = model;
					newModel.substituteAssertions(pair.getFirst(), pair.getSecond());
					TList.removeDuplicates(newModel);
					if (newModel != null) {
						try {
							TList.revalidateModel(newModel);
							newModels.add(newModel);
						} catch (ClashException e) {
							log.debug("Clash found. Model: " + newModel + " . Assertion: " + e.getAddedAssertion());
						}
					}
					++counter;
				}	
				//do not move forward
				return newModels;
			} else {
				//move forward
				model.visited(true);
				//model = model.getNext();
				//add to trigger list
				model.getRoot().getTriggerRules().addRule(amc, getIndividualA(), getDependencySet());
				//return new status
				List<TList<Assertion>> list = new ArrayList<>();
				list.add(model);
				return list;
			}
		}	
		if (concept instanceof AtLeastConcept) {
			AtLeastConcept amc = (AtLeastConcept) concept;
			Role role = amc.getRole();
			Concept c = amc.getConceptA();
			int card = amc.getCardinality();
			Map<Individual, Set<Integer>> allFillers = model.getFillersWithDependencies(role, ind);
			if (allFillers.size() < card) {
				List<Assertion> toBeAdded = new ArrayList<>(card);
				//The dependency of the new fillers is the union of the dependency sets of the AtLeast rule
				//and all the existing role filers
				//int bFactor = this.getBranchFactor();
				Set<Integer> currentDSet = new HashSet<>(this.getDependencySet());
				for (Individual i : allFillers.keySet())
					currentDSet.addAll(allFillers.get(i));
				
				for (int i = allFillers.size(); i < card; ++i) {
					//create a random individual
					Individual newInd = new Individual();
					//add C(x), R(b,x)
					toBeAdded.add(new ConceptAssertion(c, newInd, -1, currentDSet));
					toBeAdded.add(new RoleAssertion(role, getIndividualA(), newInd, -1, currentDSet));
				} 
				if (toBeAdded.size() > 0) {
					try {
						model.append(toBeAdded); //bf and ds is the same for each new chld
					} catch (ClashException e) {
						log.debug("Clash found. Model: " + model + " . Assertion: " + e.getAddedAssertion());
						log.error("You can have a clash in a AtLeast expansion rule. Something is erroneous");
						List<TList<Assertion>> list = new ArrayList<>();
						list.add(model);
						return list;				
					}
				}
			}
			model.visited(true);
			//model = model.getNext();
			List<TList<Assertion>> list = new ArrayList<>();
			list.add(model);
			return list;
		}	
		log.error("Unhandled Concept Assertion: " + this.concept.getClass());
		return null;
	}

	@Override
	public void setIndividualA(Individual ind) {
		this.ind = ind;
	}

	@Override
	public Assertion getACopy() {
		ConceptAssertion ass = new ConceptAssertion(
				this.concept, new Individual(this.ind.getName()),
				this.branchFactor, this.dependencySet);
		return ass;
	}

	@Override
	public Assertion getNegation(boolean toNNF) {
		Concept negConcept;
		if (this.concept instanceof NotConcept) {
			negConcept = this.concept.getConceptA();
		} else { 
			negConcept = new NotConcept(concept);
		}
		if (toNNF)
			negConcept = NNFFactory.getNNF(negConcept);
		
		return new ConceptAssertion(
				negConcept, new Individual(this.ind.getName()),
				this.branchFactor, this.dependencySet);
	}

}
