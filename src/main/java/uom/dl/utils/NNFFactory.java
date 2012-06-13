package uom.dl.utils;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.AtMostConcept;
import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.BinaryConcept;
import uom.dl.elements.Concept;
import uom.dl.elements.ExistsConcept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;

public class NNFFactory {
	public static Concept getNNF(Concept c) {
		if (c.isNNF())
			return c;
		
		if (c instanceof BinaryConcept)
			return getNNF((BinaryConcept)c);
		
		//1. check for negative disjunction
		if (c instanceof NotConcept) {
			Concept child = c.getConceptA();
			if (child instanceof UnionConcept){
				UnionConcept uc = (UnionConcept) child;
				Concept tmp1 = new NotConcept(uc.getConceptA());
				Concept tmp2 = new NotConcept(uc.getConceptB());
						
				Concept A = getNNF(tmp1);
				Concept B = getNNF(tmp2);
				Concept newC = new IntersectionConcept(A, B);
				return newC;
			}
			if (child instanceof IntersectionConcept){
				IntersectionConcept uc = (IntersectionConcept) child;
				Concept tmp1 = new NotConcept(uc.getConceptA());
				Concept tmp2 = new NotConcept(uc.getConceptB());
						
				Concept A = getNNF(tmp1);
				Concept B = getNNF(tmp2);
				Concept newC = new UnionConcept(A, B);
				return newC;
			}
			if (child instanceof ExistsConcept) {
				ExistsConcept ec = (ExistsConcept) child;
				Concept tmp = new NotConcept(ec.getConceptA());
				
				Concept nnfConcept = getNNF(tmp);
				Concept newC = new ForAllConcept(ec.getRole(), nnfConcept);
				return newC;
			}
			if (child instanceof ForAllConcept) {
				ForAllConcept fac = (ForAllConcept) child;
				Concept tmp = new NotConcept(fac.getConceptA());
				
				Concept nnfConcept = getNNF(tmp);
				Concept newC = new ExistsConcept(fac.getRole(), nnfConcept);
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
				return getNNF(child.getConceptA()); 
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
		Concept A = new AtomicConcept("A");
		Concept B = new AtomicConcept("B");
		Role R = new AtomicRole("R");
		
		Concept c = new NotConcept(new NotConcept(new NotConcept(new NotConcept(new IntersectionConcept(A,B)))));
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
		c = new NotConcept(new ForAllConcept(R, new NotConcept(new IntersectionConcept(A, new NotConcept(new IntersectionConcept(B, new NotConcept(A)))))));
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
		c = new NotConcept(new ExistsConcept(R, new NotConcept(new IntersectionConcept(A, B))));
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
		c = new NotConcept(new ExistsConcept(R, new NotConcept(new NotConcept(new IntersectionConcept(A, B)))));
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
		c = new NotConcept(new AtMostConcept(3, R, new NotConcept(new IntersectionConcept(A, new IntersectionConcept(B, new NotConcept(A))))));
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
		c = new IntersectionConcept(new ExistsConcept(R, A), new IntersectionConcept(new ExistsConcept(R, B), 
				new NotConcept(new ExistsConcept(R, new IntersectionConcept(A, B))))); 
		System.out.println("Normal:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		c = getNNF(c);
		System.out.println("NNF:\t" + c.toString() + ". Is in NNF: " + (c.isNNF() ? "Yes" : "No"));
		
	}

}
