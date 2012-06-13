package tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uom.dl.elements.AtomicConcept;
import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.elements.ConceptFactory;
import uom.dl.elements.Role;

public class ConceptFactoryTest {
	Concept A = new AtomicConcept("A");
	Concept B = new AtomicConcept("B");
	Role R = new AtomicRole("R");
	
	ConceptFactory factory;
	
	@Before
	public void setUp() {
		factory = new ConceptFactory();
		A = new AtomicConcept("A");
		B = new AtomicConcept("B");
		R = new AtomicRole("R");
	}
	
	@After
	public void tearDown() {		
	}
	
	@Test
	public void test_simple_union_statement() {
		Concept concept = factory.c(A).union().c(B).build(); //(A⊔B)
		assertEquals(concept.toString(), "(A⊔B)");
	}
	
	@Test
	public void test_simple_intersection_statement() {
		Concept concept = factory.c(A).intersection().c(B).build(); //(A⊓B)
		assertEquals(concept.toString(), "(A⊓B)");
	}

	@Test
	public void test_simple_not_statement() {
		Concept concept = factory.not().c(A).build(); //(¬A)
		assertEquals(concept.toString(), "¬A");
	}
	
	@Test
	public void test_simple_exists_statement() {
		Concept concept = factory.exists(R).c(A).build(); //(∃R.A)
		assertEquals(concept.toString(), "(∃R.A)");
	}
	
	@Test
	public void test_simple_forall_statement() {
		Concept concept = factory.forall(R).c(A).build(); //(∀R.A)
		assertEquals(concept.toString(), "(∀R.A)");
	}
	
	@Test
	public void test_simple_atleast_statement() {
		Concept concept = factory.atleast(3, R).c(A).build(); //(⩾3R.A)⩾⩽
		assertEquals(concept.toString(), "⩾3R.A");
	}
	
	@Test
	public void test_simple_atmost_statement() {
		Concept concept = factory.atmost(3, R).c(A).build(); //(⩽3R.A)
		assertEquals(concept.toString(), "⩽3R.A");
	}
	
	@Test
	public void test_complex_not_statement() {
		Concept concept = factory.not().start().c(A).union().c(B).end().build(); //(¬A⊔B)
		assertEquals(concept.toString(), "¬(A⊔B)");
	}
	
	@Test
	public void test_complex_atleast_statement() {
		Concept concept = factory.atleast(3, R).start().c(A).union().c(B).end().build(); //(⩾3R.A)⩾⩽
		assertEquals(concept.toString(), "⩾3R.(A⊔B)");
	}
	
	@Test
	public void test_complex_atmost_statement() {
		Concept concept = factory.atmost(3, R).start().c(A).union().c(B).end().build(); //(⩽3R.A)⩾⩽
		assertEquals(concept.toString(), "⩽3R.(A⊔B)");
	}
	
	@Test
	public void test_complex_forall_statement() {
		Concept concept = factory.forall(R).start().c(A).union().c(B).end().build(); 
		assertEquals(concept.toString(), "(∀R.(A⊔B))");
	}
	
	@Test
	public void test_complex_exists_statement() {
		Concept concept = factory.exists(R).start().c(A).union().c(B).end().build(); 
		assertEquals(concept.toString(), "(∃R.(A⊔B))");
	}
	
	@Test
	public void test_complex_intersection_union_statement() {
		Concept concept = factory.c(A).union().c(B).intersection().c(A).union().c(B).build(); //
		//assertEquals(concept.toString(), "(((A⊔B)⊓A)⊔B)");
		assertEquals(concept.toString(), "(A⊔(B⊓A)⊔B)");
	}
	

}
