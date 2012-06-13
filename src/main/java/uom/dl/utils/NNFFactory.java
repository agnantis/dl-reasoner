package uom.dl.utils;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.AtMostConcept;
import uom.dl.elements.AtomicConcept;
import uom.dl.elements.BinaryConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.UnionConcept;

public class NNFFactory {
	public static Concept getNNF(Concept c) {
		if (c instanceof BinaryConcept)
			return getNNF((BinaryConcept)c);
		
		//1. check for negative disjunction
		if (c instanceof NotConcept) {
			Concept child = c.getConceptA();
			if (child instanceof UnionConcept){
				Concept A = getNNF(((UnionConcept)child).getConceptA());
				Concept B = getNNF(((UnionConcept)child).getConceptB());
				Concept newC = new IntersectionConcept(new NotConcept(A), new NotConcept(B));
				return newC;
			}
			if (child instanceof IntersectionConcept){
				Concept A = getNNF(((IntersectionConcept)child).getConceptA());
				Concept B = getNNF(((IntersectionConcept)child).getConceptB());
				Concept newC = new UnionConcept(new NotConcept(A), new NotConcept(B));
				return newC;
			}
			if (child instanceof ExistsConcept) {
				Concept A = getNNF(((ExistsConcept)child).getConceptA());
				Concept newC = new ForAllConcept(((ExistsConcept)child).getRole(), new NotConcept(A));
				return newC;
			}
			if (child instanceof ForAllConcept) {
				Concept A = getNNF(((ForAllConcept)child).getConceptA());
				Concept newC = new ExistsConcept(((ForAllConcept)child).getRole(), new NotConcept(A));
				return newC;
			}
			if (child instanceof AtMostConcept) {
				AtMostConcept amc = (AtMostConcept) child;
				Concept A = getNNF(amc.getConceptA());
				Concept newC = new AtLeastConcept(amc.getCardinality()+1, amc.getRole(), A);
				return newC;
			}
			if (child instanceof AtLeastConcept) {
				AtLeastConcept amc = (AtLeastConcept) child;
				int card = Math.max(amc.getCardinality() - 1, 0);
				
				Concept A = getNNF(amc.getConceptA());
				Concept newC = new AtMostConcept(card, amc.getRole(), A);
				return newC;
			}
			if (child instanceof NotConcept) {
				//not not
				return child.getConceptA(); 
			}
		}
			
		return c;
	}
	
	private static Concept getNNF(BinaryConcept c) {
		if (c instanceof UnionConcept) {
			Concept A = getNNF(c.getConceptA());
			Concept B = getNNF(c.getConceptB());
			Concept newC = new UnionConcept(A, B);
			return newC;
		}
		if (c instanceof IntersectionConcept) {
			Concept A = getNNF(c.getConceptA());
			Concept B = getNNF(c.getConceptB());
			Concept newC = new IntersectionConcept(A, B);
			return newC;
		}
		assert false : "You shouldn't have reached this code. Object: " + c;
		return c;
	}
	
	public static void main(String[] args) {
		AtomicConcept A = new AtomicConcept("A");
		AtomicConcept B = new AtomicConcept("B");
		Concept c = new UnionConcept(A, B);
		System.out.println("Normal:\t" + c.toString());
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString());
	}

}
