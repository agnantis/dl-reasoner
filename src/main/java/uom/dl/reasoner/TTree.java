package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.List;

import javax.management.relation.RoleStatus;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Individual;
import uom.dl.elements.Role;
import uom.dl.reasoner.Interpretation.RoleCouple;

public class TTree<T extends Assertion> {
	public static final boolean ADD_IN_PARALLEL = true;
	public static final boolean ADD_IN_SEQUENCE = !ADD_IN_PARALLEL;
	
	private T value;
	private List<TTree<T>> children = new ArrayList<>();
	private TTree<T> parent = null;
	private boolean isExpandable = true;

	public TTree(T c) {
		this.value = c;
	}	
	
	private boolean isLeaf() {
		return children.isEmpty();
	}
	
	public List<TTree<T>> getChildren() {
		return this.children;
	}
	
	public T getValue() {
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
		T c = node.getValue();
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
	
	public List<Individual> getFiller(Role role, Concept concept, Individual ind) {
		TTree<T> current = this;
		List<Individual> candidateRoles = new ArrayList<>();
		List<Individual> candidateInds = new ArrayList<>();
		while (current != null) {
			T aValue = current.getValue();
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
	
	public boolean containsFiller(Role role, Concept concept, Individual ind) {
		TTree<T> current = this;
		List<Individual> candidateRoles = new ArrayList<>();
		List<Individual> candidateInds = new ArrayList<>();
		while (current != null) {
			T aValue = current.getValue();
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
	
	public List<Individual> getUnspecifiedFiller(Role role, Concept concept, Individual ind) {
		TTree<T> current = this;
		List<Individual> candidateRoles = new ArrayList<>();
		List<Individual> existingInds = new ArrayList<>();
		//search down
		while (current != null) {
			T aValue = current.getValue();
			if (aValue instanceof RoleAssertion) {
				RoleAssertion ra = (RoleAssertion) aValue;
				DLElement el = ra.getElement();
				if (role.equals(el) && ind.equals(ra.getIndividualA()))
					candidateRoles.add(ra.getIndividualB());
			} else if (aValue instanceof ConceptAssertion) {
				if (concept.equals(aValue.getElement()))
					existingInds.add(aValue.getIndividualA());
			}
			current = current.getParent();
		}
		//find individuals
		candidateRoles.removeAll(candidateRoles);
		return candidateRoles;
	}
	
	public String toString(){
		return "N(" + this.getValue() + ")";
	}
	
	
	public String print(){
		return this.print("");
	}
		
	
	//└──├──│
	private String print(String prefix) {
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
			s.append(child.print(prefix+"|   ")); 
		}
		if (!this.getChildren().isEmpty()) {
			TTree<T> child = this.getChildren().get(this.getChildren().size()-1);
			s.append(prefix);
			s.append("└── ");
			s.append(child.print(prefix+"    "));
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
