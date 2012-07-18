package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TList<T extends Assertion> {
	private static final Logger log = LoggerFactory.getLogger(TList.class);
	public static int NO_OF_DUPLICATES = 0;
	
	private Assertion value;
	private TList<T> next = null;
	private TList<T> previous = null;
	protected boolean isExpandable = true;
	private TListHead<T> root;
	private boolean visited = false;
	
	//backtracking info
		
	public TList(T c) {
		this.value = c;
	}	
	
	private boolean isLeaf() {
		return next == null;
	}
	
	public boolean hasNext() {
		return !isLeaf();
	}
	
	public TList<T> getNext() {
		return this.next;
	}
	
	public Assertion getValue() {
		return this.value;
	}
	
	public boolean visited() {
		return this.visited;
	}
	
	public void visited(boolean visited) {
		this.visited = visited;
	}
	
	private void addChild(TList<T> child, boolean isExpandable){
		child.previous = this;
		child.isExpandable = isExpandable;
		child.root = this.getRoot();
		this.next = child;
		//trigger?
		Assertion a = child.getValue();
		//there is a case of a null assertion, when we duplicate a TList
		//in this case there is no need to check for trigger rules
		if (a == null)
			return;
		if (a instanceof RoleAssertion){
			try {
				this.getRoot().getTriggerRules().assertionAdded((RoleAssertion) a);
			} catch (ClashException e) {
				this.getRoot().clashFound(e.getDependencyUnion());
				log.debug("Clash found. Model: " + this + " . Assertion: " + e.getAddedAssertion());
			}
		}
	}
	
	public boolean isCurrentExpandable() {
		return this.isExpandable;
	}
	
	public boolean modelExists() {
		if (isLeaf()) {
			//this node causes a clash
			return isCurrentExpandable();
		}
		//check its next
		return getNext().modelExists();
	}
	
	public boolean canBeFurtherExpanded() {
		if (!this.getValue().isAtomic())
			return true;
		if (isLeaf())
			return false;
		return this.getNext().canBeFurtherExpanded();
	}
	
	public TList<T> getPrevious() {
		return this.previous;
	}
	
	public void append(T child/*, int brancingFactor, Set<Integer> dependencySet*/) throws ClashException{
		append(child, false);
	}
	
	public void append(T child, boolean doNotCheckForDuplicate/*, int brancingFactor, Set<Integer> dependencySet*/) throws ClashException {
		List<T> children = new ArrayList<>(1);
		children.add(child);
		append(children, doNotCheckForDuplicate);
	}
	
	public void append(List<T> children/*, int brancingFactor, Set<Integer> dependencySet*/) throws ClashException{
		append(children, false);
	}
	
	public void append(List<T> children, boolean doNotCheckForDuplicate/*, int brancingFactor, Set<Integer> dependencySet*/)throws ClashException{
		if (children.isEmpty())
			return;
		TList<T> current = this;
		if (!current.isCurrentExpandable())
			return;
		
		while (!current.isLeaf()) {
			current = current.getNext();
			if (!current.isCurrentExpandable())
				return;
		}
		T child = children.remove(0);
		TList<T> n = new TList<T>(child);
		current.setNext(n, doNotCheckForDuplicate);
		current.append(children);
		
	}
	
	public boolean setNext(TList<T> node, boolean doNotCheckForDuplicate) throws ClashException {
		Assertion c = node.getValue();
		if (c == null)
			throw new NullPointerException("Concept cannot be null");
		
		//check if already exists or there is a clash
		boolean clashFound = false;
		if (c.canHaveComplement()) {
			TList<T> current = this;
			while (current != null) {
				Assertion ass = current.getValue();
				if (c.isComplement(ass)) {
					//clashFound = true;
					//break;
					ClashException ce = new ClashException(c, ass);
					this.getRoot().clashFound(ce.getDependencyUnion());
					//union of the dependency sets of the two clash concepts
					//Set<Integer> dset = new HashSet<>(ass.getDependencySet());
					//dset.addAll(c.getDependencySet());
					//ass.setDependencySet(dset);
					throw ce;
				}
				//if it already exists, do not add it
				if (!doNotCheckForDuplicate && ass.equals(c))
					return false;
				
				//check parent
				current = current.previous;				
			}
		}
		this.addChild(node, !clashFound);
		return true;
	}
	
	//public static <T extends Assertion> TList<T> removeLeaf(TList<T> tlist) {
	public TList<T> removeLeaf() {
		if (this.isLeaf()) {
			if (this.getPrevious() == null) //one and only element
				return null;
		}
		TList<T> temp = this.getLeaf().getPrevious();
		temp.setLeaf();
		return temp;
	}
	
	/**
	 * Returns all individuals x, such that:
	 * R(ind, x) AND C(x)
	 * @param role
	 * @param concept
	 * @param ind
	 * @return Returns all individuals x, such that R(ind, x) AND C(x)
	 */
	public Set<Individual> getFiller(Role role, Concept concept, Individual ind) {
		TList<T> current = this.getRoot();
		Set<Individual> candidateRoles = new HashSet<>();
		Set<Individual> candidateInds = new HashSet<>();
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA()))
					candidateRoles.add(ra.getIndividualB());
			} else if (aValue instanceof ConceptAssertion) {
				if (concept.equals(aValue.getElement()))
					candidateInds.add(aValue.getIndividualA());
			}
			current = current.getNext();
		}
		//find individuals
		candidateInds.retainAll(candidateRoles);
		return candidateInds;
	}
	
	/**
	 * Returns all individuals x, such that:
	 * R(ind, x)
	 * @param role
	 * @param ind/13072012.png
	 * @return Returns all individuals x, such that R(ind, x)
	 */
	public Set<Individual> getFillers(Role role, Individual ind) {
		return getFillersWithDependencies(role, ind).keySet();
	}
	
	public Map<Individual, Set<Integer>> getFillersWithDependencies(Role role, Individual ind) {
		TList<T> current = this.getRoot();
		//Set<Individual> roleFillers = new HashSet<>();
		Map<Individual, Set<Integer>> roleFillers = new HashMap<>();
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA()))
					roleFillers.put(ra.getIndividualB(), ra.getDependencySet());
			} 
			current = current.getNext();
		}
		return roleFillers;
	}
	
	/**
	 * Checks if there is an individual x, such that:
	 * R(ind, x) AND C(x)
	 * @param role
	 * @param concept
	 * @param ind
	 * @return true if exits, otherwise false
	 */
	public boolean containsFiller(Role role, Concept concept, Individual ind) {
		TList<T> current = this.getRoot();
		List<Individual> candidateRoles = new ArrayList<>();
		List<Individual> candidateInds = new ArrayList<>();
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA())) {
					if (candidateInds.contains(ra.getIndividualB()))
						return true;
					else
						candidateRoles.add(ra.getIndividualB());
				}
			} else if (aValue instanceof ConceptAssertion) {
				if (concept.equals(aValue.getElement())) {
					if (candidateRoles.contains(aValue.getIndividualA()))
						return true;
					else
						candidateInds.add(aValue.getIndividualA());
				}
			}
			current = current.getNext();
		}
		//find individuals
		return false;
	}
	
	/**
	 * Returns a list of all individuals x of TList, that exists an assertion R(ind,x)
	 * but not an assertion C(x) 
	 * @param role
	 * @param concept
	 * @param ind
	 * @return
	 */
	public Set<Individual> getUnspecifiedFiller(Role role, Concept c, Individual ind) {
		Map<Individual, Set<Integer>> map = getUnspecifiedFillerWithDependencies(role, c, ind);
		return map.keySet();
	}
	
	/**
	 * Returns a map which its keys are all individuals x of TList, that exists an assertion R(ind,x)
	 * but not an assertion C(x) 
	 * @param role
	 * @param concept
	 * @param ind
	 * @return
	 */
	public Map<Individual, Set<Integer>> getUnspecifiedFillerWithDependencies(Role role, Concept c, Individual ind) {
		TList<T> current = this.getRoot();
		//Map<TList<T>, List<Individual>> allCases = new HashMap<>();
		Map<Individual, Set<Integer>> candidateFillers = new HashMap<>();
		Set<Individual> existingFillers = new HashSet<>();
		//search up
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA())) {
					candidateFillers.put(ra.getIndividualB(), ra.getDependencySet());
				}
			} else  {
				DLElement el = aValue.getElement();
				if (c.equals(el)) {
					existingFillers.add(aValue.getIndividualA());
				}
			}
			current = current.getNext();
		}
		//remove existing
		for (Individual ex : existingFillers) {
			candidateFillers.remove(ex);
		}
		return candidateFillers;
	}
	
	public TListHead<T> getRoot() {
		return this.root;
	}
	
	public TList<T> getLeaf() {
		TList<T> current = this;

		while (current.hasNext()) {
			current = current.getNext();	
		}
		return current;
	}
	
	public void substituteAssertions(Individual from, Individual to) {
		TList<T> current = this.getRoot();
		while (current != null){
			Assertion assertion = current.getValue();
			if (assertion.getIndividualA().equals(from)) {
				assertion.setIndividualA(to);
			} 
			if (assertion instanceof BinaryAssertion) {
				BinaryAssertion ra = (BinaryAssertion) assertion;
				if (ra.getIndividualB().equals(from))
					ra.setIndividualB(to);
			}
			current = current.getNext();
		}
	}
	
	public static <T extends Assertion> boolean removeDuplicatesTopDown(TList<T> model) {
		boolean duplicateExists = false;
		TList<T> current = model.getRoot();
		TList<T> previous = current;
		Set<Assertion> existing = new HashSet<>();
		boolean duplicateStatus = false;
		while (current != null) {
			Assertion nodeValue = current.getValue();
			boolean notExists = existing.add(nodeValue);
			if (notExists) {
				if (duplicateStatus) {
					//previous.setNext(current);
					previous.addChild(current, current.isExpandable);
					duplicateStatus = false;
				}
				previous = current;
			} else {
				//check if *this* is a duplicate
				if (current == model) {
					if (current.getNext() != null)
						model = current.getNext();
				}
				//duplicate found
				previous.setLeaf();
				duplicateStatus = true;
				duplicateExists = true;
			}
			current = current.getNext();			
		}
		return duplicateExists;
	}
	
	public static <T extends Assertion> boolean removeDuplicates(TList<T> model) {
		boolean duplicateExists = false;
		TList<T> current = model.getLeaf();
		TList<T> lastUnique = current;
		Set<Assertion> existing = new HashSet<>();
		boolean duplicateStatus = false;
		while (current != null) {
			Assertion nodeValue = current.getValue();
			boolean notExists = existing.add(nodeValue);
			if (notExists) {
				if (duplicateStatus) {
					//previous.setNext(current);
					current.addChild(lastUnique, lastUnique.isExpandable);
					duplicateStatus = false;
				}
				lastUnique = current;
			} else {
				//check if *this* is a duplicate
				if (current == model) {
					model = lastUnique;
				}
				//duplicate found
				//previous.setLeaf();
				duplicateStatus = true;
				duplicateExists = true;
			}
			current = current.getPrevious();			
		}
		return duplicateExists;
	}
	
	private void setLeaf() {
		this.next = null;		
	}

	public boolean containsClash() {
		return this.getRoot().containsClash();
	}
	
	public static <T extends Assertion> TList<T> duplicate(TList<T> original, boolean deepCopy) {
		return duplicate(original, deepCopy, true);
	}
	
	public static <T extends Assertion> TList<T> duplicate(TList<T> original, boolean deepCopy, boolean countAsNew) {
		if (countAsNew) { ++NO_OF_DUPLICATES; }
		if (original == null)
			return null;
		
		TListHead<T> orig = original.getRoot();
		//original = original.getRoot();
		TList<T> newCopy = TListHead.duplicateMetadata(orig);
		TList<T> current = orig;
		TList<T> newCurrent = newCopy;
		//copy down
		while (current != null) {
			if (deepCopy)
				newCurrent.value = current.getValue().getACopy();
			else
				newCurrent.value = current.getValue();
			
			newCurrent.isExpandable = current.isExpandable;
			newCurrent.visited = current.visited;
			TList<T> next = new TList<>(null);
			newCurrent.addChild(next, true);
			if (current == original)
				newCopy = newCurrent;
			
			newCurrent = newCurrent.getNext();
			current = current.getNext();
		}
		//remove last empty child
		newCurrent = newCurrent.getPrevious();
		newCurrent.next = null;
				
		//copy trigger rules
		TListHead<T> root = original.getRoot();
		TListHead<T> copyRoot = newCopy.getRoot();
		TriggerRules tr = root.getTriggerRules().duplicate();
		copyRoot.triggerRules = tr;
		tr.setReceiver(copyRoot);		
		return newCopy;
	}

	/**
	 * Checks if a model is still valid (does not contain any clash). This method should be called
	 * each time an individual substitution is happening.  
	 * @param model
	 * @return
	 */
	public static <T extends Assertion> void revalidateModel(TList<T> model) throws ClashException{
		TList<T> current = model.getRoot();
		List<DLElement> atomicConcepts = new ArrayList<>();
		while (current != null) {
			current.isExpandable = true; //by default
			Assertion nodeValue = current.getValue();
			if (nodeValue.isAtomic()) {
				//check for clash
				for (DLElement c : atomicConcepts) {
					if (nodeValue.isComplement(c)) {
						//break;
						ClashException ce = new ClashException(nodeValue, (Assertion) c); 
						model.getRoot().clashFound(ce.getDependencyUnion());
						throw ce;
					}						
				}
				atomicConcepts.add(nodeValue);
			}
			current = current.getNext();
		}
		//return true;
	}
	
	public String toString(){
		//return "N(" + this.getValue() + ")";
		StringBuffer sb = new StringBuffer();
		TList<T> root = this.getRoot();
		if (root == this) 
			sb.append("*");
		if (root.visited())
			sb.append("-");
		sb.append(root.getValue());
		while (root.hasNext()) {
			sb.append(" -> ");
			root = root.getNext();
			if (root == this) 
				sb.append("*");
			if (root.visited())
				sb.append("-");
			sb.append(root.getValue());
		}
		return sb.toString();
	}
	
	/**
	 * Search its children for the assertion a, and marks it as true
	 * in case it finds it.
	 * @param a an assertion
	 * @return true if the assertion a exists, otherwise false.
	 */
	public boolean setChildVisited(Assertion a) {
		TList<T> modelPnt = this;

		while (modelPnt != null) {
			if (modelPnt.getValue().equals(a)) {
				modelPnt.visited(true);
				return true;
			}
			modelPnt = modelPnt.getNext();
		}	
		return false;
	}
	
	/*
	public String repr(){
		return this.repr("");
	}*/
		
	
	//└──├──│
	public String repr() {
		StringBuffer s = new StringBuffer();
		//s.append(prefix);
		if (!this.isCurrentExpandable())
			s.append("*");
		s.append(this.getValue().toString());
		s.append("\n");
		if (this.hasNext()) {
			TList<T> child = this.getNext();
			s.append("  |\n");
			s.append(child.repr());
		}
		return s.toString();
	}
	
	public static void main(String[] args) throws ClashException {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept B1 = new AtomicConcept("B1");
		Individual a = new Individual("a");
		Assertion a1 = new ConceptAssertion(A, a, -1, new HashSet<Integer>(0));
		Assertion a2 = new ConceptAssertion(B, a, -1, new HashSet<Integer>(0));
		Assertion a3 = new ConceptAssertion(B1, a, -1, new HashSet<Integer>(0));
		TList<Assertion> orig1 = new TList<>(a1);
		TList<Assertion> orig2 = new TList<>(a2);
		TList<Assertion> orig3 = new TList<>(a3);
		orig1.setNext(orig2, false);
		orig2.setNext(orig3, false);
		
		
		System.out.println(orig2.repr());
		TList<Assertion> copy1 = TList.duplicate(orig3, false);
		System.out.println(copy1.repr());
	}
}
