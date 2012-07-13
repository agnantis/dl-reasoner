package uom.dl.reasoner.opts;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optimizations {
	private static final Logger log = LoggerFactory.getLogger(Optimizations.class);
	
	private final Set<Optimization> activeOpts = new HashSet<>();
	
	public void setOptimization(Optimization opt, boolean value) {
		if (value) {
			activeOpts.add(opt);
		} else {
			activeOpts.remove(opt);
		}
	}
	
	public boolean usesOptimization(Optimization opt) {
		return activeOpts.contains(opt);
	}
	
	public final Set<Optimization> getActiveOptimizations() {
		return new HashSet<>(this.activeOpts); 
	}
	
	//Available optimizations
	public static enum Optimization {
		SEMANTIC_BRANCHING, MOMS_HEURISTIC, LOCAL_SIMPLIFICATION, DIRECTED_BACKTRACKING
	}
}
