package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.List;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TList<T extends Assertion> {
	
	private Assertion value;
	private TList<T> next = null;
	private TList<T> previous = null;
	private boolean isExpandable = true;
	
	public static <T extends Assertion> TList<T> duplicate(TList<T> original, boolean deepCopy) {
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
	
	private void addChild(TList<T> child, boolean isExpandable){
		child.previous = this;
		child.isExpandable = isExpandable;
		this.next = child;
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
		if (isLeaf())
			return false;
		if (!this.getValue().isAtomic())
			return true;
		return this.getNext().canBeFurtherExpanded();
	}
	
	public TList<T> getPrevious() {
		return this.previous;
	}
	
	public void append(List<T> children){
		TList<T> current = this;
		if (!current.isCurrentExpandable())
			return;
		
		while (!current.isLeaf()) {
			current = current.getNext();
			if (!current.isCurrentExpandable())
				return;
		}
		
		for (T c : children){
			TList<T> n = new TList<T>(c);
			current.setNext(n);
			current = n;
		}
		
	}
	
	public boolean setNext(TList<T> node) {
		Assertion c = node.getValue();
		if (c == null)
			throw new NullPointerException("Concept cannot be null");
		
		//check if already exists or there is a clash
		boolean clashFound = false;
		if (c.isAtomic()) {
			TList<T> current = this;
			while (current != null) {
				Assertion ass = current.getValue();
				if (c.isComplement(ass)) {
					clashFound = true;
					break;
				}
				//if it already exists, do not add it
				if (ass.equals(c))
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
	public List<Individual> getFiller(Role role, Concept concept, Individual ind) {
		TList<T> current = this.getRoot();
		List<Individual> candidateRoles = new ArrayList<>();
		List<Individual> candidateInds = new ArrayList<>();
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
	public List<Individual> getFillers(Role role, Individual ind) {
		TList<T> current = this.getRoot();
		List<Individual> roleFillers = new ArrayList<>();
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
	public List<Individual> getUnspecifiedFiller(Role role, Individual ind) {
		TList<T> current = this.getRoot();
		//Map<TList<T>, List<Individual>> allCases = new HashMap<>();
		List<Individual> candidateRoles = new ArrayList<>();
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
	
	public void substituteAssertions(Individual from, Individual to) {
		TList<T> current = this.getRoot();
		while (current != null){
			Assertion assertion = current.getValue();
			if (assertion.getIndividualA().equals(from)) {
				assertion.setIndividualA(to);
			} else if (assertion instanceof BinaryAssertion) {
				BinaryAssertion ra = (BinaryAssertion) assertion;
				if (ra.getIndividualB().equals(from))
					ra.setIndividualB(to);
			}
			current = current.getNext();
		}
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
		orig1.setNext(orig2);
		orig2.setNext(orig3);
		
		
		System.out.println(orig2.repr());
		TList<Assertion> copy1 = TList.duplicate(orig3, false);
		System.out.println(copy1.repr());
		/*
		AtomicConcept B2 = new AtomicConcept("B2");
		AtomicConcept B3 = new AtomicConcept("B3");
		AtomicConcept C = new AtomicConcept("C");
		AtomicConcept D = new AtomicConcept("D");
		AtomicConcept D1 = new AtomicConcept("D1");
		AtomicConcept D2 = new AtomicConcept("D2");
		AtomicConcept D3 = new AtomicConcept("D3");
		
		TTree nodeA = new TTree(A);
		TTree nodeB = new TTree(B);
		TTree nodeB1 = new TTree(B1);
		TTree nodeB2 = new TTree(B2);
		TTree nodeB3 = new TTree(B3);
		TTree nodeC = new TTree(C);
		TTree nodeD = new TTree(D);
		TTree nodeD1 = new TTree(D1);
		TTree nodeD2 = new TTree(D2);
		TTree nodeD3 = new TTree(D3);
		
		nodeA.add(nodeB);
		nodeB.add(nodeB1);
		nodeB.add(nodeB2);
		nodeB.add(nodeB3);
		nodeA.add(nodeC);
		nodeC.add(nodeD);
		nodeD.add(nodeD1);
		nodeD.add(nodeD2);
		nodeD.add(nodeD3);
		
		System.out.println(nodeA);
		*/
	}
}
