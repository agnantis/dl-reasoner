package uom.dl.reasoner;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uom.dl.elements.DLElement;
import uom.dl.utils.TListVisualizer;


public class Model {
	private static final Logger log = LoggerFactory.getLogger(Model.class);
	private final boolean isSatisfiable;
	private final TList<Assertion> extension;
	private Interpretation interpretation;
	
	public Model(TList<Assertion> extension, boolean isSatisfiable) {
		this.extension = extension;
		this.isSatisfiable = isSatisfiable;
	}

	public boolean isSatisfiable() {
		return isSatisfiable;
	}

	public TList<Assertion> getExtension() {
		return extension;
	}
	
	public DLElement initialConcept() {
		return this.extension.getRoot().getValue().getElement();
	}
	
	public Interpretation getInterpretation(){
		if (!isSatisfiable())
			return null;
		
		if (this.interpretation == null) {
			this.interpretation = new Interpretation();
			TList<Assertion> curNode = this.extension.getRoot(); 
			while (curNode != null) {
				Assertion assertion = curNode.getValue();
				this.interpretation.addAssertion(assertion);
				TList<Assertion> tempNode = curNode;
				curNode = tempNode.getNext();
			}			
		}
		
		return this.interpretation;
	}
	
	public void printModel(boolean showImage) {
		if (this.isSatisfiable()) {
			System.out.println("There is a model for concept: " + this.initialConcept());
		} else {
			System.out.println("There is no model for concept: " + this.initialConcept());
		}
		System.out.println("Tableaux Extension:");
		System.out.println(getExtension().getRoot().repr());
		//TListVisualizer<Assertion> visual = new TListVisualizer<Assertion>(getExtension().getRoot());
		if (showImage) {
			TListVisualizer.showGraph(getExtension().getRoot());
			//visual.showGraph();
		}
	}
	
	public static void printModel(List<Model> models, boolean showImage) {
		List<TList<Assertion>> lists = new ArrayList<>(models.size());
		for (Model m : models)
			lists.add(m.getExtension().getRoot());
		
		if (showImage)
			TListVisualizer.showGraph(lists);
	}
}
