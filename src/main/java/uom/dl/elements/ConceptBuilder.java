package uom.dl.elements;


public class ConceptBuilder {
	Concept concept;
	private int nest;
	private ConceptBuilder parent;
	private ConceptReceiver conceptReciever;
	private boolean isComplement = false;
	
	public ConceptBuilder(ConceptBuilder parent) {
		this.parent = parent;
	}
	
	public ConceptBuilder() {
		this(null);
	}
	
	public Concept build(){
		if (this.nest > 0)
			throw new RuntimeException("Wrong number of parenthesis. Too many openings");
		return this.concept;
	}
	
	public ConceptBuilder start() {
		//++this.nest;
		ConceptBuilder inner = new ConceptBuilder(this);
		return inner;
	}
	
	public ConceptBuilder end() {
		//--this.nest;
		//if (this.nest < 0)
		//	throw new RuntimeException("Wrong number of parenthesis. Too many closings");
		//this.parent.concept = this.concept;
		this.parent.c(this.concept);
		return this.parent;
	}
	
	public ConceptBuilder not() {
		this.isComplement = !this.isComplement; //in case of a -(-A)
		//this.conceptReciever = new Not();	
		return this;
		/*
		if (this.conceptReciever == null) {
			this.conceptReciever = new Not();	
			return this;
		} else {
			ConceptFactory cf = start();
			cf.not();
			return cf;
		}*/
		
	}
	
	public ConceptBuilder union() {
		checkForComplement();
		this.conceptReciever = new Union(this.concept);
		return this;
	}

	private boolean checkForComplement() {
		if (this.isComplement) {
			if (this.concept != null)
				this.concept = new NotConcept(this.concept);
			this.isComplement = false;
			return true;
		}
		return false;
	}
	
	public ConceptBuilder intersection() {
		checkForComplement();
		this.conceptReciever = new Intersection(this.concept);
		return this;
	}
	
	public ConceptBuilder forall(Role r) {
		this.conceptReciever = new ForAll(r, checkForComplement());
		return this;
	}
	
	public ConceptBuilder exists(Role r) {
		
		this.conceptReciever = new Exists(r, checkForComplement());
		return this;
	}
	
	public ConceptBuilder atleast(int num, Role r) {
		this.conceptReciever = new AtLeast(num, r, checkForComplement());
		return this;
	}
	
	public ConceptBuilder atmost(int num, Role r) {
		this.conceptReciever = new AtMost(num, r, checkForComplement());
		return this;
	}
	
	public ConceptBuilder c(Concept c) {
		if (this.isComplement) {
			c = new NotConcept(c);
			this.isComplement = false;
		}
		if (this.conceptReciever == null) {
			this.concept = c;
		} else {
			this.conceptReciever.addConcept(c);
			Concept cTemp = this.conceptReciever.getConcept();
			this.concept = cTemp;
			this.conceptReciever = null;
		}
		return this;
	}
	
	public static void main(String[] args) {
		ConceptBuilder cf1 = new ConceptBuilder();
		//ConceptBuilder cf2 = new ConceptBuilder();
		
		Concept A = new AtomicConcept("A");
		Concept B = new AtomicConcept("B");
		Role R = new AtomicRole("R");
		
		ConceptBuilder c;
		
		c = cf1.start().exists(R).c(A).end()
		.intersection()
		.start().exists(R).c(B).end()
		.intersection()
		.start().not().exists(R)
			.start().c(A).intersection().c(B).end()
		.end();//(((∃R.A)⊓(∃R.B))⊓¬(∃R.(A⊓B)))
		//c = cf1.start().not().exists(R).c(A);//¬(∃R.A)
		//c = cf1.not().c(A).union().c(B); //(¬A⊔B)
		//c = cf1.not().c(A).union().not().c(B); //(¬A⊔¬B)
		//c = cf1.not().start().c(A).union().not().c(B).end(); //¬(A⊔¬B)
		//c = cf1.c(A).intersection().not().start().c(B).union().c(A).end(); //(A⊓¬(B⊔A))
		System.out.println(c.build());
	}
	private interface ConceptReceiver {
		public void addConcept(Concept c);
		public Concept getConcept();
	}
	
	private static class Exists implements ConceptReceiver {
		private Role r = null;
		private Concept c = null;
		private boolean isComplement;
		
		public Exists(Role r, boolean isComplement) {
			this.r = r;
			this.isComplement = isComplement;
		}
		
		public void addConcept(Concept c) {
			assert this.c == null;
			this.c = c;
		}
		
		public Concept getConcept() {
			assert this.r != null;
			assert this.c != null;
			Concept c1 = new ExistsConcept(r, c);
			if (this.isComplement)
				return new NotConcept(c1);
			return c1;
		}

	}
	
	private static class ForAll implements ConceptReceiver {
		private Role r = null;
		private Concept c = null;
		private boolean isComplement;
		
		public ForAll(Role r, boolean isComplement) {
			this.r = r;
			this.isComplement = isComplement;
		}
		
		public void addConcept(Concept c) {
			assert this.c == null;
			this.c = c;
		}
		
		public Concept getConcept() {
			assert this.r != null;
			assert this.c != null;
			Concept c1 = new ForAllConcept(r, c);
			if (this.isComplement)
				return new NotConcept(c1);
			return c1;
		}
	}

	private static class Union implements ConceptReceiver {
		private Concept c = null;
		private Concept d = null;
		
		public Union(Concept c) {
			this.c = c;
		}
		
		public void addConcept(Concept d) {
			assert this.d == null;
			this.d = d;
		}
		
		public UnionConcept getConcept() {
			assert this.c != null;
			assert this.d != null;
			return new UnionConcept(c, d);
		}

	}
	
	private static class Intersection implements ConceptReceiver {
		private Concept c = null;
		private Concept d = null;
		
		public Intersection(Concept c) {
			this.c = c;
		}
		
		public void addConcept(Concept d) {
			assert this.d == null;
			this.d = d;
		}
		
		public IntersectionConcept getConcept() {
			assert this.c != null;
			assert this.d != null;
			return new IntersectionConcept(c, d);
		}
	}
	
	private static class AtMost implements ConceptReceiver {
		private int num = -1;
		private Concept c = null;
		private boolean isComplement;
		private Role r;
		
		public AtMost(int num, Role r, boolean isComplement) {
			 this.num = num;
			 this.r = r;
			 this.isComplement = isComplement;
		}
		
		public void addConcept(Concept c) {
			assert this.c == null;
			this.c = c;
		}
		
		public Concept getConcept() {
			assert this.c != null;
			assert this.num != -1;
			Concept cTemp = new AtMostConcept(this.num, this.r, this.c);
			if (this.isComplement)
				return new NotConcept(cTemp);
			return cTemp;
		}

	}
	
	private static class AtLeast implements ConceptReceiver {
		private int num = -1;
		private Concept c = null;
		private Role r;
		private boolean isComplement;
		
		public AtLeast(int num, Role r, boolean isComplement) {
			 this.num = num;
			 this.r = r;
			 this.isComplement = isComplement;
		}
		
		public void addConcept(Concept c) {
			assert this.c == null;
			this.c = c;
		}
		
		public Concept getConcept() {
			assert this.c != null;
			assert this.num != -1;
			Concept c1 = new AtLeastConcept(this.num, this.r, this.c);
			if (this.isComplement)
				return new NotConcept(c1);
			return c1;
		}
	}
	
}
