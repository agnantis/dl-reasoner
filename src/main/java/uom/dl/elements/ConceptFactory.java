package uom.dl.elements;


public class ConceptFactory {
	Concept concept;
	private int nest;
	private ConceptFactory parent;
	private ConceptReceiver conceptReciever;
	private boolean isComplement = false;
	
	public ConceptFactory(ConceptFactory parent) {
		this.parent = parent;
	}
	
	public ConceptFactory() {
		this(null);
	}
	
	public Concept build(){
		if (this.nest > 0)
			throw new RuntimeException("Wrong number of parenthesis. Too many openings");
		return this.concept;
	}
	
	public ConceptFactory start() {
		//++this.nest;
		ConceptFactory inner = new ConceptFactory(this);
		return inner;
	}
	
	public ConceptFactory end() {
		//--this.nest;
		//if (this.nest < 0)
		//	throw new RuntimeException("Wrong number of parenthesis. Too many closings");
		//this.parent.concept = this.concept;
		this.parent.c(this.concept);
		return this.parent;
	}
	
	public ConceptFactory not() {
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
	
	public ConceptFactory union() {
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
	
	public ConceptFactory intersection() {
		checkForComplement();
		this.conceptReciever = new Intersection(this.concept);
		return this;
	}
	
	public ConceptFactory forall(Role r) {
		this.conceptReciever = new ForAll(r, checkForComplement());
		return this;
	}
	
	public ConceptFactory exists(Role r) {
		
		this.conceptReciever = new Exists(r, checkForComplement());
		return this;
	}
	
	public ConceptFactory atleast(int num, Role r) {
		this.conceptReciever = new AtLeast(num, r, checkForComplement());
		return this;
	}
	
	public ConceptFactory atmost(int num, Role r) {
		this.conceptReciever = new AtMost(num, r, checkForComplement());
		return this;
	}
	
	public ConceptFactory c(Concept c) {
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
		ConceptFactory cf1 = new ConceptFactory();
		ConceptFactory cf2 = new ConceptFactory();
		
		Concept A = new BaseConcept("A");
		Concept B = new BaseConcept("B");
		Role R = new BaseRole("R");
		
		ConceptFactory c;
		
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
