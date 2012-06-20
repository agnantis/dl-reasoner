package uom.dl.reasoner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.AtomicRole;
import uom.dl.elements.Concept;
import uom.dl.utils.TreeVisualizer;


public class Model {
	private static final Logger log = LoggerFactory.getLogger(Model.class);
	private final boolean isSatisfiable;
	private final TTree<ConceptAssertion> extension;
	private Interpretation interpretation;
	
	public Model(TTree<ConceptAssertion> extension, boolean isSatisfiable) {
		this.extension = extension;
		this.isSatisfiable = isSatisfiable;
	}

	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	public TTree<ConceptAssertion> getExtension() {
		return extension;
	}
	
	public Concept initialConcept() {
		return this.extension.getValue().getConcept();
	}
	
	public Interpretation getInterpretation(){
		if (!isSatisfiable())
			return null;
		
		if (this.interpretation == null) {
			this.interpretation = new Interpretation();
			ConceptAssertion currentNode = this.extension.getValue();
			while (currentNode != null) {
				if (currentNode.isAtomic()) {
					if (currentNode instanceof AtomicRole) {
						
					}
				}
			}
		}
		
		return this.interpretation;
	}
	
	public void printModel(boolean showImage) {
		if (this.isSatisfiable()) {
			System.out.println("There is no model for concept: " + this.initialConcept());
		} else {
			System.out.println("There is a model for concept: " + this.initialConcept());
		}
		System.out.println("Tableaux Extension:");
		System.out.println(getExtension().print());
		TreeVisualizer<ConceptAssertion> visual = new TreeVisualizer<ConceptAssertion>(getExtension());
		System.out.println(visual.toDotFormat());
		if (showImage)
			visual.showGraph();
	}

}
