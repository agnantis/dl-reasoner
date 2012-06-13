package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import uom.dl.elements.AtLeastConcept;
import uom.dl.elements.AtMostConcept;
import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.elements.ForAllConcept;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.NotConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;
import uom.dl.utils.NNFFactory;

public class NNFFactoryTest {

	private Concept A, B, C; 
	private Role R, F;

	@Before
	public void setUp() throws Exception {
		A = new AtomicConcept("A");
		B = new AtomicConcept("B");
		C = new AtomicConcept("C");
		R = new AtomicRole("R");
		F = new AtomicRole("F");
	}
	
	@Test
	public void testGetNNFNotConcept() {
		Concept c_old = new NotConcept(A);
		Concept c_new = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotNotConcept() {
		Concept c_old = new NotConcept(new NotConcept(A));
		Concept c_new = A;
		c_old = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}

	@Test
	public void testGetNNFUnionConcept() {
		Concept c_old = new UnionConcept(A, B);
		Concept c_new = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotUnionConcept() {
		Concept c_old = new NotConcept(new UnionConcept(A, B));
		Concept c_new = new IntersectionConcept(new NotConcept(A), new NotConcept(B));
		c_old = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFIntersectionConcept() {
		Concept c_old = new IntersectionConcept(A, B);
		Concept c_new = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotIntersectionConcept() {
		Concept c_old = new NotConcept(new IntersectionConcept(A, B));
		Concept c_new = new UnionConcept(new NotConcept(A), new NotConcept(B));
		c_old = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFAtMostConcept() {
		Concept c_old = new AtMostConcept(3, R);
		Concept c_new = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotAtMostConcept() {
		Concept c_old = new NotConcept(new AtMostConcept(3, R));
		Concept c_new = new AtLeastConcept(4, R);
		c_old = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFAtLeastConcept() {
		Concept c_old = new AtLeastConcept(3, R);
		Concept c_new = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotAtLeastConcept1() {
		Concept c_old = new NotConcept(new AtLeastConcept(3, R));
		Concept c_new = new AtMostConcept(2, R);
		c_old = NNFFactory.getNNF(c_old);
		assertEquals(c_old, c_new);		
	}
	
	@Test
	public void testGetNNFNotAtLeastConcept2() {
		Concept c_old = new NotConcept(new AtLeastConcept(3, R));
		Concept c_new = new AtMostConcept(2, F);
		c_old = NNFFactory.getNNF(c_old);
		assertThat("Different roles", c_old, is(not(c_new)));
	}
	
	@Test
	public void testGetNNFComplexConcept() {
		Concept c_old = new NotConcept(new ForAllConcept(R, new NotConcept(new UnionConcept(A, B))));
		System.out.println(c_old);
		System.out.println(NNFFactory.getNNF(c_old));
		//Concept c_new = new AtLeastConcept(4, R);
		//c_old = NNFFactory.getNNF(c_old);
		//assertEquals(c_old, c_new);		
	}

}
