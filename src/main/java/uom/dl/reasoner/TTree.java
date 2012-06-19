package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.List;

import uom.dl.elements.Concept;
import uom.dl.utils.ConceptFactory;

public class TTree {
	public static final boolean ADD_IN_PARALLEL = true;
	public static final boolean ADD_IN_SEQUENCE = !ADD_IN_PARALLEL;
	
	private Concept value;
	private List<TTree> children = new ArrayList<>();
	private TTree parent = null;
	private boolean isExpandable = true;

	public TTree(Concept c) {
		this.value = c;
	}	
	
	private boolean isLeaf() {
		return children.isEmpty();
	}
	
	public List<TTree> getChildren() {
		return this.children;
	}
	
	public Concept getValue() {
		return this.value;
	}
	
	private void addChild(TTree child, boolean isExpandable){
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
		for (TTree child : this.getChildren()) {
			if (child.modelExists()) {
				//there is at least one child that contains a valid model
				return true;
			}
		}
		//there is no child or each child contains a clash
		return false;
	}
	
	public TTree getParent() {
		return this.parent;
	}
	
	public void append(List<Concept> children, boolean inParallel){
		List<TTree> frontier = new ArrayList<>();
		frontier.add(this);
		while (!frontier.isEmpty()) {
			TTree tree = frontier.remove(0);
			if (tree.isLeaf() && tree.isExpandable()) {
				if (inParallel == TTree.ADD_IN_PARALLEL) {
					for (Concept c: children) {
						TTree n = new TTree(c);
						tree.add(n);
					}
				} else {
					for (Concept c: children) {
						TTree n = new TTree(c);
						tree.add(n);
						tree = n;
					}
				}
			} else {
				frontier.addAll(tree.getChildren());
			}
		}
	}

	public boolean add(TTree node) {
		Concept c = node.getValue();
		if (c == null)
			throw new NullPointerException("Concept cannot be null");
		
		//check if already exists or there is a clash
		boolean clashFound = false;
		if (c.isAtomic()) {
			TTree current = this;
			while (current != null) {
				if (ConceptFactory.isComplement(c, current.getValue())) {
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
}
