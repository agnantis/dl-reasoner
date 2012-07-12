package uom.dl.reasoner;

import uom.dl.reasoner.opts.Optimizations;

public class TableuaxConfiguration {
	private static final TableuaxConfiguration configuration = new TableuaxConfiguration();
	private final Optimizations optimizations;
	
	
	private TableuaxConfiguration() {
		this.optimizations = new Optimizations();
	}
	
	public static TableuaxConfiguration getConfiguration() {
		return configuration;
	}

	public Optimizations getOptimizations() {
		return optimizations;
	}
}
