package uom.dl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uom.dl.reasoner.Assertion;
import uom.dl.reasoner.TList;

public class TreeVisualizer {
	private static Random RND = new Random();
	private List<TreeVisualizer> children = new ArrayList<>();
	private Assertion value;
	private final int id;
	//private Set<TListVisualizer> parent
	
	public TreeVisualizer(Assertion value) {
		this.value = value;
		this.id = RND.nextInt();
	}
	
	public List<TreeVisualizer> getChildren() {
		return this.children;
	}
	
	public void addChild(TreeVisualizer child) {
		this.children.add(child);		
	}
	
	public Assertion getValue() {
		return this.value;
	}
	
	public void setValue(Assertion value) {
		this.value = value;
	}
	
	public int getId() {
		return this.id;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeVisualizer))
			return false;
		return this.value.equals(((TreeVisualizer)obj).value);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	public static <T extends Assertion> TreeVisualizer buildModelTree(List<TList<T>> models) {
		TreeVisualizer tVis = new TreeVisualizer(null);
		for (TList<T> tlist : models) {
			TreeVisualizer current = tVis;
			current.setValue(tlist.getValue());
			while (tlist.hasNext()) {
				tlist = tlist.getNext();
				TreeVisualizer child = new TreeVisualizer(tlist.getValue());
				int index = current.getChildren().indexOf(child);
				if (index > -1) {
					current = current.getChildren().get(index);
				} else {
					current.addChild(child);
					current = child;				
				}
			}		
			if (tlist.containsClash()) {
				TreeVisualizer child = new TreeVisualizer(null);
				current.addChild(child);
			}
		}
		
		return tVis;
	}
	
	

}
