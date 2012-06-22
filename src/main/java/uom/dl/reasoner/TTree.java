package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;

public class TTree<T extends Assertion> {
	public static final boolean ADD_IN_PARALLEL = true;
	public static final boolean ADD_IN_SEQUENCE = !ADD_IN_PARALLEL;
	
	private Assertion value;
	private List<TTree<T>> children = new ArrayList<>();
	private TTree<T> parent = null;
	private boolean isExpandable = true;
	
	//A copy constructor;
	
	public TTree(TTree<T> tree, boolean bothDirections) {
		//this(tree.getValue());
		///*TTree<T> newTree*/ this = new TTree<>(value);
		this.value = tree.getValue().getACopy();
		for (TTree<T> child : tree.getChildren()) {
			TTree<T> newChild = new TTree<T>(child, false);
			this.add(newChild);
		}
		this.isExpandable = tree.isExpandable;	
		if (bothDirections) {
			TTree<T> parent = tree.getParent();
			TTree<T> current = this; 
			while (parent != null) {
				TTree<T> newParent = new TTree<T>();
				newParent.value = parent.getValue().getACopy();
				newParent.add(current);
				current = parent;
				parent = parent.getParent();
			}
		}
	}
	
	private TTree() {
		this((T)null);
	}

	public TTree(T c) {
		this.value = c;
	}	
	
	private boolean isLeaf() {
		return children.isEmpty();
	}
	
	public List<TTree<T>> getChildren() {
		return this.children;
	}
	
	public Assertion getValue() {
		return this.value;
	}
	
	private void addChild(TTree<T> child, boolean isExpandable){
		child.parent = this;
		child.isExpandable = isExpandable;
		this.children.add(child);
	}
	
	public boolean isExpandable() {
		return this.isExpandable;
	}
	
	public boolean modelExists() {
		if (!isExpandable()) {
			//this node causes a clash
			return false;
		}
		for (TTree<T> child : this.getChildren()) {
			if (child.modelExists()) {
				//there is at least one child that contains a valid model
				return true;
			}
		}
		
		//there is no child 
		if (this.getChildren().isEmpty())
			return isExpandable();
		//or each child contains a clash
		return false;
	}
	
	public TTree<T> getParent() {
		return this.parent;
	}
	
	public void append(List<T> children, boolean inParallel){
		List<TTree<T>> frontier = new ArrayList<>();
		frontier.add(this);
		while (!frontier.isEmpty()) {
			TTree<T> tree = frontier.remove(0);
			//check if already exists
			if (tree.isLeaf() && tree.isExpandable()) {
				if (inParallel == TTree.ADD_IN_PARALLEL) {
					for (T c: children) {
						TTree<T> n = new TTree<T>(c);
						tree.add(n);
					}
				} else {
					for (T c: children) {
						TTree<T> n = new TTree<T>(c);
						tree.add(n);
						tree = n;
					}
				}
			} else {
				frontier.addAll(tree.getChildren());
			}
		}
	}
	
	public boolean add(TTree<T> node) {
		Assertion c = node.getValue();
		if (c == null)
			throw new NullPointerException("Concept cannot be null");
		
		//check if already exists or there is a clash
		boolean clashFound = false;
		if (c.isAtomic()) {
			TTree<T> current = this;
			while (current != null) {
				if (c.isComplement(current.getValue())) {
				//if (ConceptFactory.isComplement(c, current.getValue())) {
					clashFound = true;
				}
				if (current.getValue().equals(c))
					return false;
				
				//check parent
				current = current.parent;				
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
		TTree<T> current = this;
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
			current = current.getParent();
		}
		//find individuals
		candidateInds.retainAll(candidateRoles);
		return candidateInds;
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
		TTree<T> current = this;
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
			current = current.getParent();
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
	public Map<TTree<T>, List<Individual>> getUnspecifiedFiller(Role role, Individual ind) {
		Map<TTree<T>, List<Individual>> allCases = new HashMap<>();
		TTree<T> current = this;
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
			/*else if (aValue instanceof ConceptAssertion) {
				if (concept.equals(aValue.getElement()))
					existingInds.add(aValue.getIndividualA());
			}*/
			current = current.getParent();
		}
		//find individuals
		//candidateRoles.removeAll(candidateRoles);
		allCases.put(this, candidateRoles);
		//search down
		List<TTree<T>> frontier = new ArrayList<>();
		List<TTree<T>> childs = this.getChildren();
		frontier.addAll(childs);
		while (!frontier.isEmpty()) {
			current = frontier.remove(0);
			Assertion aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA())) {
					List<Individual> theList = allCases.get(current);
					if (theList == null)
						theList = new ArrayList<>();
					theList.add(ra.getIndividualB());	
					allCases.put(current, theList);
				}
			} 
			frontier.addAll(current.getChildren());
			
		}
		return allCases;
	}
	
	public TTree<T> getRoot() {
		TTree<T> current = this;

		while (current.getParent() != null) {
			current = current.getParent();	
		}
		return current;
	}
	
	public String toString(){
		return "N(" + this.getValue() + ")";
	}
	
	
	public String repr(){
		return this.repr("");
	}
		
	
	//└──├──│
	private String repr(String prefix) {
		StringBuffer s = new StringBuffer();
		//s.append(prefix);
		if (!this.isExpandable())
			s.append("*");
		s.append(this.getValue().toString());
		s.append("\n");
		//for (TTree child : this.getChildren()) {
		for (int i = 0; i < this.getChildren().size()-1; ++i) {
			TTree<T> child = this.getChildren().get(i);
			s.append(prefix);
			s.append("├── ");
			s.append(child.repr(prefix+"|   ")); 
		}
		if (!this.getChildren().isEmpty()) {
			TTree<T> child = this.getChildren().get(this.getChildren().size()-1);
			s.append(prefix);
			s.append("└── ");
			s.append(child.repr(prefix+"    "));
		}
		return s.toString();
	}
	
	public static void main(String[] args) {
		/*
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		AtomicConcept B1 = new AtomicConcept("B1");
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
