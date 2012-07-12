package uom.dl.reasoner.opts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Optimizations {
	private static final Logger log = LoggerFactory.getLogger(Optimizations.class);
	
	public static final String SEMANTIC_BRANCHING = "semantic_branching";
	public static final String MOMS_HEURISTIC = "moms_heuristics";
	public static final String LOCAL_SIMPLIFICATION = "local_simplification";
	private boolean semanticBranchingOpt = true;
	private boolean momsHeuristic = false;
	private boolean localSimplification = true;
	

	public void setOptimization(String name, boolean value) {
		switch (name) {
		case SEMANTIC_BRANCHING:
			this.semanticBranchingOpt = value;
			break;
		case MOMS_HEURISTIC:
			this.momsHeuristic = value;
		case LOCAL_SIMPLIFICATION:
			this.localSimplification = value;
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
		case LOCAL_SIMPLIFICATION:
			return localSimplification;
		default:
			log.warn("There is no such otimization: " + name);
			return false;
		}
	}
}
