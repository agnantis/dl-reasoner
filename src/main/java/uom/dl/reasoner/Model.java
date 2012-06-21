package uom.dl.reasoner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.Concept;
import uom.dl.elements.DLElement;
import uom.dl.elements.Role;
import uom.dl.utils.TreeVisualizer;


public class Model {
	private static final Logger log = LoggerFactory.getLogger(Model.class);
	private final boolean isSatisfiable;
	private final TTree<Assertion> extension;
	private Interpretation interpretation;
	
	public Model(TTree<Assertion> extension, boolean isSatisfiable) {
		this.extension = extension;
		this.isSatisfiable = isSatisfiable;
	}

	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	public TTree<Assertion> getExtension() {
		return extension;
	}
	
	public DLElement initialConcept() {
		return this.extension.getValue().getElement();
	}
	
	public Interpretation getInterpretation(){
		if (!isSatisfiable())
			return null;
		
		if (this.interpretation == null) {
			this.interpretation = new Interpretation();
			TTree<Assertion> curNode = this.extension; 
			while (curNode != null) {
				Assertion assertion = curNode.getValue();
				this.interpretation.addAssertion(assertion);
				TTree<Assertion> tempNode = curNode;
				curNode = null;
				for (TTree<Assertion> a : tempNode.getChildren()) {
					if (a.isExpandable()) {
						curNode = a;
						break;
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
		System.out.println(getExtension().repr());
		TreeVisualizer<Assertion> visual = new TreeVisualizer<Assertion>(getExtension());
		if (showImage)
			visual.showGraph();
	}

}
