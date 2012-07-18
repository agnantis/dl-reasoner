package uom.dl.reasoner;

import uom.dl.reasoner.opts.Optimizations;
import uom.dl.reasoner.opts.Optimizations.Optimization;

public class TableauxConfiguration {
	private static final TableauxConfiguration configuration = new TableauxConfiguration();
	private final Optimizations optimizations;
	
	
	private TableauxConfiguration() {
		this.optimizations = new Optimizations();
		this.optimizations.setOptimization(Optimization.SEMANTIC_BRANCHING, true);
		this.optimizations.setOptimization(Optimization.MOMS_HEURISTIC, true);
		this.optimizations.setOptimization(Optimization.DIRECTED_BACKTRACKING, false);
	}
	
	public static TableauxConfiguration getConfiguration() {
		return configuration;
	}

	public Optimizations getOptimizations() {
		return optimizations;
	}
	
	public static boolean usesOptimization(Optimization opt) {
		return getConfiguration().getOptimizations().usesOptimization(opt);
	}
}
