package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TList<T extends Assertion> {
	public static int NO_OF_DUPLICATES = 0;
	
	private Assertion value;
	private TList<T> next = null;
	private TList<T> previous = null;
	private boolean isExpandable = true;
	private TriggerRules triggerRules;
	
	public static <T extends Assertion> TList<T> duplicate(TList<T> original, boolean deepCopy) {
		++NO_OF_DUPLICATES;
		if (original == null)
			return null;
		
		//original = original.getRoot();
		TList<T> newCopy = new TList<>();
		TList<T> current = original;
		TList<T> newCurrent = newCopy;
		//copy down
		while (current != null) {
			if (deepCopy)
				newCurrent.value = current.getValue().getACopy();
			else
				newCurrent.value = current.getValue();
			newCurrent.isExpandable = current.isExpandable;
			TList<T> next = new TList<>();
			newCurrent.addChild(next, true);
			newCurrent = newCurrent.getNext();
			current = current.getNext();
		}
		//remove last empty child
		newCurrent = newCurrent.getPrevious();
		newCurrent.next = null;
		//copy up
		current = original;
		newCurrent = newCopy;
		while (current.previous != null) {
			current = current.previous;
			TList<T> tmp = new TList<>();
			if (deepCopy)
				tmp.value = current.getValue().getACopy();
			else
				tmp.value = current.getValue();
			tmp.isExpandable = current.isExpandable;
			tmp.addChild(newCurrent, newCurrent.isExpandable);
			newCurrent = newCurrent.previous;
		}
		
		//copy trigger rules
		TList<T> root = original.getRoot();
		TList<T> copyRoot = newCopy.getRoot();
		TriggerRules tr = root.getTriggerRules().duplicate();
		copyRoot.triggerRules = tr;
		tr.setReceiver((TList<Assertion>) copyRoot);		
		return newCopy;
	}
		
		
	private TList() {
		this(null);
	}

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
	
	public TriggerRules getTriggerRules() {
		if (this.getPrevious() != null)
			return this.getPrevious().getTriggerRules();
		if (this.triggerRules == null) {
			this.triggerRules = new TriggerRules();
			this.triggerRules.setReceiver((TList<Assertion>) this);
		}
		return this.triggerRules;
	}
	
	private void addChild(TList<T> child, boolean isExpandable){
		child.previous = this;
		child.isExpandable = isExpandable;
		this.next = child;
		//trigger?
		Assertion a = child.getValue();
		//there is a case of a null assertion, when we duplicate a TList
		//in this case there is no need to check for trigger rules
		if (a == null)
			return;
		if (a instanceof RoleAssertion){
			this.getTriggerRules().assertionAdded((RoleAssertion) a);
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
	
	public void append(T child) {
		append(child, false);
	}
	
	public void append(T child, boolean doNotCheckForDuplicate) {
		List<T> children = new ArrayList<>(1);
		children.add(child);
		append(children, doNotCheckForDuplicate);
		
	}
	
	public void append(List<T> children){
		append(children, false);
	}
	
	public void append(List<T> children, boolean doNotCheckForDuplicate){
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
	
	private boolean setNext(TList<T> node, boolean doNotCheckForDuplicate) {
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
					clashFound = true;
					break;
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
	 * @param ind
	 * @return Returns all individuals x, such that R(ind, x)
	 */
	public Set<Individual> getFillers(Role role, Individual ind) {
		TList<T> current = this.getRoot();
		Set<Individual> roleFillers = new HashSet<>();
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA()))
					roleFillers.add(ra.getIndividualB());
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
	 * Returns a map with key a TTree object and its corresponding value
	 * a list of all individuals x which belong to the TTree and R(ind,x)
	 * @param role
	 * @param ind
	 * @return
	 */
	public Set<Individual> getUnspecifiedFiller(Role role, Individual ind) {
		TList<T> current = this.getRoot();
		//Map<TList<T>, List<Individual>> allCases = new HashMap<>();
		Set<Individual> candidateRoles = new HashSet<>();
		//search up
		while (current != null) {
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA()))
					candidateRoles.add(ra.getIndividualB());
			} 
			current = current.getNext();
		}
		return candidateRoles;
	}
	
	public TList<T> getRoot() {
		TList<T> current = this;

		while (current.getPrevious() != null) {
			current = current.getPrevious();	
		}
		return current;
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
		TList<T> current = this.getRoot();
		while (current != null) {
			if (!current.isCurrentExpandable())
				return true;
			current = current.getNext();
		}
		return false;
	}

	/**
	 * Checks if a model is still valid (does not contain any clash). This method should be called
	 * each time an individual substitution is happening.  
	 * @param model
	 * @return
	 */
	public static <T extends Assertion> boolean revalidateModel(TList<T> model) {
		TList<T> current = model.getRoot();
		List<DLElement> atomicConcepts = new ArrayList<>();
		while (current != null) {
			current.isExpandable = true; //by default
			Assertion nodeValue = current.getValue();
			if (nodeValue.isAtomic()) {
				//check for clash
				boolean clashFound = false;
				for (DLElement c : atomicConcepts) {
					if (nodeValue.isComplement(c)) {
						clashFound = true;
						break;
					}						
				}
				if (clashFound) {
					//remove next assertions
					//TODO: should I really remove others?
					current.next = null;
					current.isExpandable = false;
					//to avoid memory leaks, like: 
					//model=(AUB)(b)->A(b)->(-A(b))->*B(b)->C(a) then 
					//model=(AUB)(b)->A(b)->*(-A(b)), otherwise its behavior is nondeterministic
					model = current;
					return false;
				} else { //add it to existing atomic concepts
					atomicConcepts.add(nodeValue);
				}
			}
			current = current.getNext();
		}
		return true;
	}
	
	public String toString(){
		//return "N(" + this.getValue() + ")";
		StringBuffer sb = new StringBuffer();
		TList<T> root = this.getRoot();
		if (root == this) 
			sb.append("*");
		sb.append(root.getValue());
		while (root.hasNext()) {
			sb.append(" -> ");
			root = root.getNext();
			if (root == this) 
				sb.append("*");
			sb.append(root.getValue());
		}
		return sb.toString();
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
	
	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept B1 = new AtomicConcept("B1");
		Individual a = new Individual("a");
		Assertion a1 = new ConceptAssertion(A, a);
		Assertion a2 = new ConceptAssertion(B, a);
		Assertion a3 = new ConceptAssertion(B1, a);
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
