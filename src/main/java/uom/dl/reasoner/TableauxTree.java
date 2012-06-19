package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
			runTableauxNode(node);
		} catch (ClashException e) {
			log.error("This should not happen to the parent node: " + e.getMessage());
		}
		return false;
	}
	
	public static boolean runTableauxNode(Node node) {
		while (!node.isEmpty()) {
			boolean clash = false;
			Concept c = node.pop();
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
				if (c.isAtomic()) { continue; }
				concepts = ConceptFactory.getUnionConcepts(c);
				Node branch = new Node(node);
				if (concepts.size() > 1) {
					for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
						try {
							branch.add(it.next());
						} catch (ClashException e) {
							log.debug(e.getMessage());
							clash = true;
						}
					}
				}
			}
		}
		
		
		return false;
	}
	
	public static boolean runTableauxForConcept(Concept concept) {
		List<TTree> frontier = new ArrayList<>();
		TTree tree = new TTree(concept);
		frontier.add(tree);
		while (!frontier.isEmpty()) {
			TTree current = frontier.remove(0);
			//execute intersection rule
			Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(current.getValue());
			if (concepts.size() > 1) {
				current.append(new ArrayList<>(concepts), TTree.ADD_IN_PARALLEL);
			}
			//execute union rule
			concepts = ConceptFactory.getUnionConcepts(current.getValue());
			if (concepts.size() > 1) {
				current.append(new ArrayList<>(concepts), TTree.ADD_IN_SEQUENCE);
			}
			//check if a model exists
			if (!current.modelExists()) {
				log.info("No model exists. Concept is unsatisfiable: " + concept);
				return false;
			}
			if (!current.getChildren().isEmpty())
				frontier.addAll(current.getChildren());
		}				
		return true;
	}
	
	private static Node execIntersectionRule(Node node) {
		//if (node.isEmpty())
		//	return null;
		Concept c = node.pop();
		boolean foundNonAtomicConcept = !c.isAtomic();
		while (!foundNonAtomicConcept) {
			if (!node.isEmpty())
				c = node.pop();
			else
				break;
			foundNonAtomicConcept = !c.isAtomic();
		} 
		if (!foundNonAtomicConcept)
			return null;
		
		Set<Concept> concepts = ConceptFactory.getIntersectionConcepts(c);
		//execute intersection rule
		if (concepts.size() > 1) {
			for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
				try {
					node.add(it.next());
				} catch (ClashException e) {
					log.debug(e.getMessage());
					return null;
				}
			}
		}
		return node;
	}
	
	private static List<Node> execUnionRule(Node node) {
		//if (node.isEmpty())
		//	return;
		
		List<Node> children = new ArrayList<>();
		Concept c = node.pop();
		boolean foundNonAtomicConcept = !c.isAtomic();
		while (!foundNonAtomicConcept) {
			if (!node.isEmpty())
				c = node.pop();
			else
				break;
			foundNonAtomicConcept = !c.isAtomic();
		} 
		if (!foundNonAtomicConcept)
			return null;
		
		Set<Concept> concepts = ConceptFactory.getUnionConcepts(c);
		if (concepts.size() > 1) {
			for (Iterator<Concept> it = concepts.iterator(); it.hasNext();) {
				try {
					Node branch = new Node(node);
					branch.add(it.next());
					//no clash happened -> added to the children list
					children.add(branch);
				} catch (ClashException e) {
					log.debug(e.getMessage());
				}
			}
		}
		//add parent also
		children.add(node);
		return children;		
	}
}
