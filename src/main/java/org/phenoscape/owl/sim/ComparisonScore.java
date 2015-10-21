package org.phenoscape.owl.sim;

public interface ComparisonScore<ID> {
	
	public ID id();

	public double similarity();

	public ID queryProfile();

	public ID corpusProfile();

}
