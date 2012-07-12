package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.elements.ConceptBuilder;
import uom.dl.elements.IntersectionConcept;
import uom.dl.elements.Role;
import uom.dl.elements.UnionConcept;

public class ConceptBuilderTest {
	Concept A, B, C;
	Role R;
	
	ConceptBuilder factory;
	
	@Before
	public void setUp() {
		factory = new ConceptBuilder();
		A = new AtomicConcept("A");
		B = new AtomicConcept("B");
		C = new AtomicConcept("C");
		R = new AtomicRole("R");
	}
	
	@After
	public void tearDown() {		
	}
	
	@Test
	public void test_simple_union_statement() {
		Concept concept = factory.c(A).union().c(B).build(); //(A⊔B)
		assertEquals(concept.toString(), "A⊔B");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_simple_intersection_statement() {
		Concept concept = factory.c(A).intersection().c(B).build(); //(A⊓B)
		assertEquals(concept.toString(), "A⊓B");
		assertTrue(concept.isNNF());
	}

	@Test
	public void test_simple_not_statement() {
		Concept concept = factory.not().c(A).build(); //(¬A)
		assertEquals(concept.toString(), "¬A");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_simple_exists_statement() {
		Concept concept = factory.exists(R).c(A).build(); //(∃R.A)
		assertEquals(concept.toString(), "∃R.A");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_simple_forall_statement() {
		Concept concept = factory.forall(R).c(A).build(); //(∀R.A)
		assertEquals(concept.toString(), "∀R.A");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_simple_atleast_statement() {
		Concept concept = factory.atleast(3, R).c(A).build(); //(⩾3R.A)⩾⩽
		assertEquals(concept.toString(), "⩾3R.A");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_simple_atmost_statement() {
		Concept concept = factory.atmost(3, R).c(A).build(); //(⩽3R.A)
		assertEquals(concept.toString(), "⩽3R.A");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_not_statement1() {
		Concept concept = factory.not().start().c(A).union().c(B).end().build(); //(¬A⊔B)
		assertEquals(concept.toString(), "¬(A⊔B)");
		assertFalse(concept.isNNF());
	}
	
	@Test
	public void test_complex_not_statement2() {
		Concept concept = factory.not().c(A).union().not().c(B).build(); //(¬A⊔B)
		assertEquals(concept.toString(), "¬A⊔¬B");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_atleast_statement() {
		Concept concept = factory.atleast(3, R).start().c(A).union().c(B).end().build(); //(⩾3R.A)⩾⩽
		assertEquals(concept.toString(), "⩾3R.(A⊔B)");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_atmost_statement() {
		Concept concept = factory.atmost(3, R).start().c(A).union().c(B).end().build(); //(⩽3R.A)⩾⩽
		assertEquals(concept.toString(), "⩽3R.(A⊔B)");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_forall_statement() {
		Concept concept = factory.forall(R).start().c(A).union().c(B).end().build(); 
		assertEquals(concept.toString(), "∀R.(A⊔B)");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_exists_statement() {
		Concept concept = factory.exists(R).start().c(A).union().c(B).end().build(); 
		assertEquals(concept.toString(), "∃R.(A⊔B)");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_complex_intersection_union_statement() {
		Concept concept = factory.c(A).union().c(B).intersection().c(A).union().c(B).build(); //
		//assertEquals(concept.toString(), "(((A⊔B)⊓A)⊔B)");
		//assertEquals(concept.toString(), "(A⊔(B⊓A)⊔B)");
		assertTrue(concept.isNNF());
	}
	
	@Test
	public void test_union_associative_property() {
		Concept concept1 = new UnionConcept(A, new UnionConcept(B, C));
		Concept concept2 = new UnionConcept(new UnionConcept(A, B), C);
		assertTrue(concept1.equals(concept2));
	}
	
	@Test
	public void test_union_associative_property_random_order() {
		Concept concept1 = new UnionConcept(A, new UnionConcept(B, C));
		Concept concept2 = new UnionConcept(new UnionConcept(C, B), A);
		assertTrue(concept1.equals(concept2));
	}
	
	@Test
	public void test_intersection_associative_property() {
		Concept concept1 = new IntersectionConcept(A, new IntersectionConcept(B, C));
		Concept concept2 = new IntersectionConcept(new IntersectionConcept(A, B), C);
		assertTrue(concept1.equals(concept2));
	}
	
	@Test
	public void test_intersectio_associative_property_random_order() {
		Concept concept1 = new IntersectionConcept(A, new IntersectionConcept(B, C));
		Concept concept2 = new IntersectionConcept(new IntersectionConcept(C, B), A);
		assertTrue(concept1.equals(concept2));
	}
	

}
