package uom.dl.reasoner;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.utils.ConceptFactory;

public class TableauxTree {
	private static Logger log = LoggerFactory.getLogger(TableauxTree.class);
	
	private TableauxTree childA = null;
	private TableauxTree childB = null;
	private TableauxTree parent = null;
	private Concept value = null;
	
	
	public TableauxTree(Concept value) {
		this(null, null, null);
		this.value = value;
	}
	
	public TableauxTree() {
		this(null, null, null);
	}
	
	public TableauxTree(TableauxTree parent) {
		this(parent, null, null);
	}
	
	public TableauxTree(TableauxTree parent, TableauxTree childA, TableauxTree childB) {
		this.parent = parent;
		this.childA = childA;
		this.childB = childB;
	}
	
	public TableauxTree getChildA() {
		return childA;
	}

	public void setChildA(TableauxTree childA) {
		this.childA = childA;
	}

	public TableauxTree getChildB() {
		return childB;
	}

	public void setChildB(TableauxTree childB) {
		this.childB = childB;
	}

	public TableauxTree getParent() {
		return parent;
	}

	public void setParent(TableauxTree parent) {
		this.parent = parent;
	}
	
	public Concept getValue() {
		return value;
	}

	public void setValue(Concept value) {
		this.value = value;
	}

	public static boolean runTableaux(Concept c) {
		//execute intersection rule
		TableauxTree ttree = new TableauxTree(c);
		Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(c);
		if (concepts.size() > 1) {
			for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
				TableauxTree child = new TableauxTree(ttree);
				ttree.setChildA(child);
				ttree = child;
			}
		}
		
		
		return false;
	}
	
	public static boolean runTableauxNode(Concept c) {
		Node node = new Node(null);
		try {
			node.add(c);
		} catch (ClashException e) {
			log.error("This should not happen to the parent node: " + e.getMessage());
		}
		while (!node.isEmpty()) {
			boolean clash = false;
			c = node.pop();
			Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(c);
			//execute intersection rule
			if (concepts.size() > 1) {
				for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
					try {
						node.add(it.next());
					} catch (ClashException e) {
						log.debug(e.getMessage());
						clash = true;
						break;
					}
				}
			}
			//execute union rule
			while (!node.isEmpty()) {
				c = node.pop();
				concepts = ConceptFactory.getUnionConcepts(c);
				Node branch = new Node(node);
				if (concepts.size() > 1) {
					for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
						branch.add(it.next());
					}
				}
			}
		}
		
		
		return false;
	}

	

}
