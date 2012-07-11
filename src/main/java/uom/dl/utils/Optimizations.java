package uom.dl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Optimizations {
	private static final Logger log = LoggerFactory.getLogger(Optimizations.class);
	
	public static final String SEMANTIC_BRANCHING = "semantic_branching";
	public static final String MOMS_HEURISTIC = "moms_heuristics";
	private boolean semanticBranchingOpt = true;//default: on
	private boolean momsHeuristic = true;//default: on
	

	public void setOptimization(String name, boolean value) {
		switch (name) {
		case SEMANTIC_BRANCHING:
			this.semanticBranchingOpt = value;
			break;
		case MOMS_HEURISTIC:
			this.momsHeuristic = value;
		default:
			log.warn("There is no such otimization: " + name);
			break;
		}
	}
	
	public boolean usesOptimization(String name) {
		switch (name) {
		case SEMANTIC_BRANCHING:
			return semanticBranchingOpt;
		case MOMS_HEURISTIC:
			return momsHeuristic;
		default:
			log.warn("There is no such otimization: " + name);
			return false;
		}
	}
}
