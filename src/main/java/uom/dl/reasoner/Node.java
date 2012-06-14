package uom.dl.reasoner;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uom.dl.elements.Concept;
import uom.dl.utils.ConceptFactory;

public class Node {
	private Node parent;
	private List<Concept> values = new ArrayList<>();
	private Set<Concept> atomics = new HashSet<>();
	
	public Node(Node parent) {
		this.parent = parent;
	}
	
	public boolean isEmpty() {
		return values.isEmpty();
	}
	
	public Concept pop() {
		return values.get(0);		
	}
	
	public boolean add(Concept c) throws ClashException {
		if (c == null)
			throw new NullPointerException("Concept cannot be null");
		
		//check if already exists or there is a clash
		if (c.isAtomic()) {
			Node current = this;
			while (current != null) {
				for (Iterator<Concept> it = current.atomics.iterator(); it.hasNext();) {
					Concept existing = it.next();
					if (ConceptFactory.isComplement(c, existing)) {
						throw new ClashException(c);
					}
					if (existing.equals(c))
						return false;
				}
				//check parent
				current = current.parent;				
			}
			//concept or its complement do not exist. Add it
			this.atomics.add(c);
		}
		this.values.add(c);
		return true;
	}
}
